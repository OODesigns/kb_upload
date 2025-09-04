# AWS Lambda Chat Bot Categorization Model

This model is designed to be used with [Apache OpenNLP](https://opennlp.apache.org/),
a machine learning toolkit for processing natural language text. The purpose of
this application is to automate the creation of a categorization model.

This project deploys multiple AWS Lambda functions using AWS SAM (Serverless Application
Model) and coordinates them with AWS Step Functions to validate, transform, and
create the chat bot model. Notifications are sent via SNS to alert on the success or
failure of the model creation process.

## 🐾 What is a Categorization Model in OpenNLP?

A categorization model (sometimes called a document categorizer) is a machine learning
model that takes text as input and predicts which category it belongs to.

**For example:**

- If you give it "yes, of course", the model might predict the category `confirmation`.
- If you give it "check the Jira backlog", the model might predict `jira`.

It’s essentially teaching the computer: “when you see these kinds of words, they usually
mean X”.

OpenNLP does this by analyzing the features of the text (words, frequencies, patterns)
and learning statistical associations between those features and your categories.

## 📂 Training Data Format

OpenNLP expects one training example per line. Each line begins with the category label.
After the label, you provide a piece of text (words, phrases, tokens) that belongs to that
category.

**Format:**

    (category) (text token)

**Example cat.txt:**

    confirmation np nbd ofc ik ikr yw rgr yepyep yessirski yezzir yh yeh ya ye yah ...
    Jira Jira, Atlassian, issues, stories, backlog, sprint, epic, workflow, board,
    Scrum ...

- Category 1: `confirmation` → contains many slang words and affirmations.
- Category 2: `jira` → contains Agile and Jira-related terminology.

Once trained, OpenNLP produces a `.bin` model file. You can then run:

    opennlp Doccat mymodel.bin "issues"

Output:

    Jira 0.92

Means: It's 92% confident the input belongs to the `jira` category.

## Implementation Overview

This repository contains an AWS SAM template that provisions the following components:

- **Lambda Functions:**
  - `ValidationFunction`: Validates the structure of the chat bot knowledge file (`knowledge.json`).
  - `TransformationFunction`: Transforms the validated file into an intermediate format.
  - `ModelFunction`: Creates the final categorization model (`cat.bin`) using the
    transformed data.
- **AWS Step Functions:**
  - Orchestrates the workflow via a state machine that includes validation, transformation,
    and model creation steps.
  - Implements error handling and notifications via SNS.
- **EventBridge Rule:**
  - Listens for S3 object creation events and triggers the state machine.
- **SNS Notifications:**
  - Sends email alerts regarding model creation success or failure.
- **IAM Roles/Policies:**
  - Grant least-privilege permissions to each component.

## Architecture

The application is architected to be scalable and maintainable. It leverages AWS Lambda for
serverless computing, AWS Step Functions for orchestration, and AWS SNS for notifications.
The architecture is outlined below:

- **S3 Upload Trigger:**
  - When a file (typically `knowledge.json`) is uploaded to the source S3 bucket, an
    EventBridge rule detects the event and triggers the state machine.
- **Validation Stage:**
  - The state machine invokes `ValidationFunction`, checking file integrity.
  - On failure, error is caught and a failure notification is sent.
- **Transformation Stage:**
  - On success, `TransformationFunction` converts the data and writes output to staging S3.
- **Model Creation:**
  - `ModelFunction` builds the final categorization model, storing it in the model bucket.
- **Notification:**
  - SNS notifies success or failure, including detailed status to the configured email.

### Diagram of Workflow

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

See the **design/** folder for a UML diagram (`*.puml`).

## Setup & Deployment

### Prerequisites
- **AWS CLI**: Installed and configured.
- **AWS SAM CLI**: For building and deploying.
- **Java 20+**: Lambda code is in Java 20+. Ensure your build環境 matches this.

### Deployment Steps

1. **Clone the Repository:**

    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. **Build the Application:**
   Compile and package Java code as in your build scripts. This usually creates a ZIP (see
   `CodeUri` in SAM pointing to `lib/build/distributions/lib.zip`).

3. **Deploy Using SAM:**
   Run the guided deployment command and follow prompts to set params such as S3 buckets and email:

    ```sh
    sam deploy --guided
    ```

    During deployment, you may set:
      - Source bucket: `s3-knowledge-upload`
      - Staging bucket: `s3-knowledge-staging`
      - Model bucket: `s3-knowledge-model`
      - SNS Email Endpoint:`emailaddress@domain.com`
   

4. **Verification:**
   - Upload a valid `knowledge.json` to the source bucket.
   - Monitor Step Functions in AWS Console.
   - Check CloudWatch logs for debugging Lambda executions.
   - Confirm email notification of model creation status.

### Testing

To manually test the workflow:

- **Upload Event Trigger:**
    Upload a properly formatted `knowledge.json` to the source bucket to start the state machine.

- **Monitor Execution:**
    Check execution results in AWS Step Functions. CloudWatch logs provide debugging.

- **Error Handling:**
    Try uploading a malformed `knowledge.json` to verify that error capture and SNS failure 
    notification are functioning as intended.
