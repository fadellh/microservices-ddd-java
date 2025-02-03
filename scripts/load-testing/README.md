
## Run locust
locust -f locust-v1.py

Go to http://localhost:8089 to start your load test, configure the number of users (concurrency), and spawn rate.

## GCS Token
gcloud auth print-access-token 