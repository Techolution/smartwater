docker login -u _json_key -p "$(cat /Users/techo-admin/Documents/Techolution/smartwater.devops/service-accounts/gcr/synthetic-eon-241312-5981fa843f37.json)" https://gcr.io

## docker command for connectionstatistics micro-service
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/connectionstatistics:2 .
docker push gcr.io/synthetic-eon-241312/smartwater/connectionstatistics:2
docker run -it gcr.io/synthetic-eon-241312/smartwater/connectionstatistics:2
kubectl -n dev set image deployment connectionstatistics connectionstatistics=gcr.io/synthetic-eon-241312/smartwater/connectionstatistics:2

## docker command for consolidateddetails micro-service
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/consolidateddetails:3 .
docker push gcr.io/synthetic-eon-241312/smartwater/consolidateddetails:3
docker run -it gcr.io/synthetic-eon-241312/smartwater/consolidateddetails:3
kubectl -n dev set image deployment consolidateddetails consolidateddetails=gcr.io/synthetic-eon-241312/smartwater/consolidateddetails:3

## docker command for mapdataservice micro-service
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/mapdataservice:2 .
docker push gcr.io/synthetic-eon-241312/smartwater/mapdataservice:2
docker run -it gcr.io/synthetic-eon-241312/smartwater/mapdataservice:2
kubectl -n dev set image deployment mapdataservice mapdataservice=gcr.io/synthetic-eon-241312/smartwater/mapdataservice:2

## docker command for notificationservice micro-service
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/notificationservice:2 .
docker push gcr.io/synthetic-eon-241312/smartwater/notificationservice:2
docker run -it gcr.io/synthetic-eon-241312/smartwater/notificationservice:2
kubectl -n dev set image deployment notificationservice notificationservice=gcr.io/synthetic-eon-241312/smartwater/notificationservice:2

## docker command for reportingservice micro-service
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/reportingservice:1 .
docker push gcr.io/synthetic-eon-241312/smartwater/reportingservice:1
docker run -it gcr.io/synthetic-eon-241312/smartwater/reportingservice:1
kubectl -n dev set image deployment reportingservice reportingservice=gcr.io/synthetic-eon-241312/smartwater/reportingservice:2

## docker command for supplydataservice micro-service
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/supplydataservice:2 .
docker push gcr.io/synthetic-eon-241312/smartwater/supplydataservice:2
docker run -it gcr.io/synthetic-eon-241312/smartwater/supplydataservice:2
kubectl -n dev set image deployment supplydataservice supplydataservice=gcr.io/synthetic-eon-241312/smartwater/supplydataservice:2

## docker command for smart-water ui service
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/ui:2 .
docker push gcr.io/synthetic-eon-241312/smartwater/ui:2
docker run -it gcr.io/synthetic-eon-241312/smartwater/ui:2
kubectl -n dev set image deployment ui ui=gcr.io/synthetic-eon-241312/smartwater/ui:2

docker build --file ubuntuDockerfile --tag ubuntu-jdk:1 .
docker run -it ubuntu-jdk:1