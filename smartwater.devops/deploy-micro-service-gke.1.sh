directoryBasePath='/Users/techo-admin/Documents/Techolution/smartwater'
connectionstatisticsDirectoryPath=$directoryBasePath/connectionstatistics
consolidateddetailsDirectoryPath=$directoryBasePath/consolidateddetails
mapdataserviceDirectoryPath=$directoryBasePath/mapdataservice
notificationserviceDirectoryPath=$directoryBasePath/notificationservice
reportingserviceDirectoryPath=$directoryBasePath/reportingservice
supplydataserviceDirectoryPath=$directoryBasePath/supplydataservice

dockerImageGCRBasePath=asia.gcr.io/anthos-lab-usdc/smartwater
imageTagVersion=20

cd $connectionstatisticsDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag $dockerImageGCRBasePath/connectionstatistics:$imageTagVersion .
docker push $dockerImageGCRBasePath/connectionstatistics:$imageTagVersion


cd $consolidateddetailsDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag $dockerImageGCRBasePath/consolidateddetails:$imageTagVersion .
docker push $dockerImageGCRBasePath/consolidateddetails:$imageTagVersion


cd $mapdataserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag $dockerImageGCRBasePath/mapdataservice:$imageTagVersion .
docker push $dockerImageGCRBasePath/mapdataservice:$imageTagVersion


cd $notificationserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag $dockerImageGCRBasePath/notificationservice:$imageTagVersion .
docker push $dockerImageGCRBasePath/notificationservice:$imageTagVersion


cd $reportingserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag $dockerImageGCRBasePath/reportingservice:$imageTagVersion .
docker push $dockerImageGCRBasePath/reportingservice:$imageTagVersion


cd $supplydataserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag $dockerImageGCRBasePath/supplydataservice:$imageTagVersion .
docker push $dockerImageGCRBasePath/supplydataservice:$imageTagVersion
