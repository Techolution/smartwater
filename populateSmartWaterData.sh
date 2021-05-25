currentDate=$(date +"%Y-%m-%d")
echo "Date is"+$currentDate
echo $currentDate
startTime="T00:00:00.000z"
endTime="T23:59:59.000z"
dataStartTime=$currentDate$startTime
dataEndTime=$currentDate$endTime

java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 1234 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 1245 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 1256 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 1267 $dataStartTime $dataEndTime -1 86400000

java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 1234 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 1245 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 1256 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 1267 $dataStartTime $dataEndTime -1 86400000

java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 1234 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 1245 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 1256 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 1267 $dataStartTime $dataEndTime -1 7200000

java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 1234 $dataStartTime $dataEndTime -1 3600000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 1245 $dataStartTime $dataEndTime -1 3600000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 1256 $dataStartTime $dataEndTime -1 3600000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 1267 $dataStartTime $dataEndTime -1 3600000


java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 2022 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 2023 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 12345 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteron 2025 $dataStartTime $dataEndTime -1 86400000



java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 2022 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 2023 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 12345 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData meteroff 2025 $dataStartTime $dataEndTime -1 86400000



java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 2022 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 2023 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 12345 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData battery 2025 $dataStartTime $dataEndTime -1 7200000


java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 2022 $dataStartTime $dataEndTime -1 3600000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 2023 $dataStartTime $dataEndTime -1 3600000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 12345 $dataStartTime $dataEndTime -1 3600000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateData flow 2025 $dataStartTime $dataEndTime -1 3600000