## Set current working project in gcloud cli
gcloud config set project synthetic-eon-241312
gcloud config set project anthos-lab-usdc
gcloud config get-value project

## Setup kubernetes cluster
gcloud container clusters get-credentials smart-water --region=us-central1-a