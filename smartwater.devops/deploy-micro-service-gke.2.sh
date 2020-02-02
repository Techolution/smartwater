dockerImageGCRBasePath=asia.gcr.io/anthos-lab-usdc/smartwater
imageTagVersion=20

docker push $dockerImageGCRBasePath/connectionstatistics:$imageTagVersion
docker push $dockerImageGCRBasePath/consolidateddetails:$imageTagVersion
docker push $dockerImageGCRBasePath/mapdataservice:$imageTagVersion
docker push $dockerImageGCRBasePath/notificationservice:$imageTagVersion
docker push $dockerImageGCRBasePath/reportingservice:$imageTagVersion
docker push $dockerImageGCRBasePath/supplydataservice:$imageTagVersion
