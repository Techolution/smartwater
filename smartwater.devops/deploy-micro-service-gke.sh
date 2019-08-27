directoryBasePath='/Users/techo-admin/Documents/Techolution/smartwater'
connectionstatisticsDirectoryPath=$directoryBasePath/connectionstatistics
consolidateddetailsDirectoryPath=$directoryBasePath/consolidateddetails
mapdataserviceDirectoryPath=$directoryBasePath/mapdataservice
notificationserviceDirectoryPath=$directoryBasePath/notificationservice
reportingserviceDirectoryPath=$directoryBasePath/reportingservice
supplydataserviceDirectoryPath=$directoryBasePath/supplydataservice

imageTagVersion=7

cd $connectionstatisticsDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/connectionstatistics:$imageTagVersion .
docker push gcr.io/synthetic-eon-241312/smartwater/connectionstatistics:$imageTagVersion
kubectl -n dev set image deployment connectionstatistics connectionstatistics=gcr.io/synthetic-eon-241312/smartwater/connectionstatistics:$imageTagVersion

cd $consolidateddetailsDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/consolidateddetails:$imageTagVersion .
docker push gcr.io/synthetic-eon-241312/smartwater/consolidateddetails:$imageTagVersion
kubectl -n dev set image deployment consolidateddetails consolidateddetails=gcr.io/synthetic-eon-241312/smartwater/consolidateddetails:$imageTagVersion

cd $mapdataserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/mapdataservice:$imageTagVersion .
docker push gcr.io/synthetic-eon-241312/smartwater/mapdataservice:$imageTagVersion
kubectl -n dev set image deployment mapdataservice mapdataservice=gcr.io/synthetic-eon-241312/smartwater/mapdataservice:$imageTagVersion

cd $notificationserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/notificationservice:$imageTagVersion .
docker push gcr.io/synthetic-eon-241312/smartwater/notificationservice:$imageTagVersion
kubectl -n dev set image deployment notificationservice notificationservice=gcr.io/synthetic-eon-241312/smartwater/notificationservice:$imageTagVersion

cd $reportingserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/reportingservice:$imageTagVersion .
docker push gcr.io/synthetic-eon-241312/smartwater/reportingservice:$imageTagVersion
kubectl -n dev set image deployment reportingservice reportingservice=gcr.io/synthetic-eon-241312/smartwater/reportingservice:$imageTagVersion

cd $supplydataserviceDirectoryPath
mvn clean
mvn compile
mvn -Dmaven.test.skip=true package
docker build --file Dockerfile --tag gcr.io/synthetic-eon-241312/smartwater/supplydataservice:$imageTagVersion .
docker push gcr.io/synthetic-eon-241312/smartwater/supplydataservice:$imageTagVersion
kubectl -n dev set image deployment supplydataservice supplydataservice=gcr.io/synthetic-eon-241312/smartwater/supplydataservice:$imageTagVersion