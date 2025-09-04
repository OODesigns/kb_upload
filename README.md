# AWS Lambda Chat Bot Categorization Model

This model is to be used by https://opennlp.apache.org/ The Apache OpenNLP library is a machine learning based toolkit for the processing of natural language text.

I created this application, so I could automate the creation of a model.

This project deploys multiple AWS Lambda functions using AWS SAM (Serverless Application Model) and coordinates them with AWS Step Functions to validate, transform, and create the chat bot model. Notifications are integrated via SNS to alert on success or failure of the model creation process.

# 🐾 What is a Categorization Model in OpenNLP?

A categorization model (sometimes called a document categorizer) is a type of machine learning model that takes text as input and predicts which category it belongs to.

For example:

If you give it "yes, of course", the model might predict the category confirmation.

If you give it "check the Jira backlog", the model might predict jira.

It’s essentially teaching the computer: “when you see these kinds of words, they usually mean X”.

OpenNLP does this by looking at the features of the text (like words, frequencies, and patterns) and learning statistical associations between those features and your defined categories.

# 📂 Training Data Format

OpenNLP expects one training example per line.

Each line begins with the category label.

After the label, you write a piece of text (words, phrases, tokens) that belongs to that category.

So the structure looks like:
   (category) (text token) 

✅ Example File cat.txt
    
    confirmation np nbd ofc ik ikr yw rgr yepyep yessirski yezzir yh yeh ya ye yah ... + more    
    Jira Jira, Atlassian, issues, stories, backlog, sprint, epic, workflow, board, Scrum ... + more
    
    Category 1: confirmation → contains a ton of slang/words that mean “yes” or “confirmed.”
    Category 2: jira → contains Agile and Jira-related terminology.

Once you train this with OpenNLP, you’ll have a .bin model file. Then you can run something like:
 
    opennlp Doccat mymodel.bin "issues"

Output would be something like:
   
    Jira 0.92

Meaning: it’s 92% confident the input belongs to the Jira category.

# Implementation Overview

This repository contains an AWS SAM template that provisions the following key components:

    Lambda Functions:

        ValidationFunction: Validates the structure of the chat bot knowledge file (knowledge.json).

        TransformationFunction: Transforms the validated file into an intermediate format.

        ModelFunction: Creates the final categorization model (cat.bin) using the transformed data.

    AWS Step Functions:

        Orchestrates the workflow through a defined state machine that includes validation, 
        transformation, and model creation steps.

        Implements error handling to capture issues at any step of the process and notifies via SNS.

    EventBridge Rule:

        Listens for object creation events in the source S3 bucket and triggers the state machine.

    SNS Notifications:

        Sends an email alert to a predefined endpoint regarding the success or failure of model creation.

    IAM Roles and Managed Policies:

        Ensures each component has the minimum required permissions for secure operation.

# Architecture
The application architecture is designed to be scalable and maintainable. 
It leverages AWS Lambda for serverless computing, AWS Step Functions for orchestration, and AWS SNS for notifications. 
The architecture is structured as follows in Text form to illustrate the flow of data and operations within the system.:


    S3 Upload Trigger:
    When a new file (typically the knowledge.json) is uploaded to the SourceBucket, an EventBridge rule detects 
    the event and triggers the AWS Step Functions state machine.

    Validation Stage:
    The state machine first invokes the ValidationFunction which checks the file for structural and content integrity. 
    If validation fails, the error is caught, and the flow branches to notify failure.

    Transformation Stage:
    On successful validation, the TransformationFunction converts the data and writes the transformed 
    output to a staging S3 bucket.

    Model Creation:
    The ModelFunction then consumes the staged data to create the final categorization model, saving it 
    to the Model S3 bucket.

    Notification:
    On process completion, an SNS topic is used to notify success or failure. The notifications include details 
    about the model status sent to the configured email endpoint.

# The diagram below summarizes the workflow:

     +-----------------------------+
     | S3 Upload (knowledge.json)  |  
     +-------------+---------------+
                   │
                   ▼
     +-----------------------------+
     |(EventBridge Rule Triggered) |      
     |     Start State Machine     |
     +-------------+---------------+
                   │
                   ▼
     +-----------------------------+ No
     │      Validate Schema        │ ───────────────────────────
     +-------------+---------------+         │                  │  
                   │                         │                  │
              Yes  ▼                         │                  ▼  
     +------------------------------+ No     │     +-------------------------+
     | Transform to Processing file | ───────      |    Exception Handling   |
     +-------------+----------------+        │      +-------------+-----------+
              Yes  │                         │                 │
                   ▼                         │                 ▼
     +-----------------------------+ No      │     +-------------------------+
     |   Create Model from file    |  ───────      |  SNS: Notify Failure    |
     +-------------+---------------+               +-------------+-----------+
              Yes  │
                   ▼
          SNS: Notify Success
                   │
                   ▼
                  End

A more detail flow can be found in **design** folder using https://plantuml.com/ UML files.

# Setup & Deployment
Prerequisites

    AWS CLI: Ensure you have the AWS CLI installed and configured with your credentials.

    AWS SAM CLI: Install the AWS SAM CLI for building and deploying the application.

    Java 20+: The Lambda functions are written in Java 20+. Make sure your build environment is set up accordingly.

Deployment Steps

    Clone the Repository:

git clone <repository-url>
cd <repository-directory>

Build the Application:

Compile and package your Java code as described in your build scripts. Typically, this involves creating a ZIP file (as seen by the CodeUri pointing to lib/build/distributions/lib.zip).

Deploy Using SAM:

Run the guided deployment command and follow the prompts to set parameters such as S3 bucket names and email subscription endpoint:

    sam deploy --guided

    During the deployment process, you can modify default values like the source bucket (s3-knowledge-upload), staging bucket (s3-knowledge-staging), and model bucket (s3-knowledge-model).

    Verification:

        Upload a valid knowledge.json file to the source bucket.

        Monitor the AWS Step Functions execution via the AWS Console.

        Check the CloudWatch logs for each Lambda function for debugging and tracking purposes.

        Verify that an email is received confirming the success or failure of the model creation process.

Testing

To manually test the full workflow:

    Upload Event Trigger:
    Place a properly formatted knowledge.json into the SourceBucket. This action should trigger the entire state machine.

    Monitor Execution:
    Check the execution results in AWS Step Functions. Utilize CloudWatch logs for detailed debugging information for each state/step.

    Error Handling:
    Intentionally provide a malformed knowledge.json to see how the error is captured and reported. The state machine is designed to catch errors and publish a failure message via SNS.

