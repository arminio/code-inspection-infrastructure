# code-inspection

Install Serverless by (see www.serverless.com):
>npm install serverless -g

For now, you may build and deploy the serverless stack using the following command:
$>(export WEBHOOK_SECRET=1234567890; export PAT=<insert-github-pat>; sbt assembly && serverless deploy)


To create the codebuild projects:
codebuild CLI command to build the jar (via sbt assembly) 
`project_root$> aws codebuild create-project --cli-input-json file://ci/codebuild/sbt-assembly/sbt-assembly.json --profile <profile> --region eu-west-2`

codebuild CLI command to deploy the serverless stack (API Gateway/Lambda) 
`project_root$> aws codebuild create-project --cli-input-json file://ci/codebuild/serverless-deploy/serverless-deploy.json --profile <profile> --region eu-west-2`