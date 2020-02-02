currentDate=$(date +"%Y-%m-%d")
echo "Date is"+$currentDate
echo $currentDate
startTime="T00:00:00.000z"
endTime="T23:59:59.000z"
dataStartTime=$currentDate$startTime
dataEndTime=$currentDate$endTime
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteron 1234 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteron 1245 $dataStartTime $dataEndTime -1 86400000

java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteron 1256 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteron 1267 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteroff 1234 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteroff 1245 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteroff 1256 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a meteroff 1267 $dataStartTime $dataEndTime -1 86400000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a battery 1234 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a battery 1245 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a battery 1256 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a battery 1267 $dataStartTime $dataEndTime -1 7200000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a flow 1234 $dataStartTime $dataEndTime -1 3600000

java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a flow 1245 $dataStartTime $dataEndTime -1 3600000
java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDat
a flow 1256 $dataStartTime $dataEndTime -1 3600000
#java -cp /home/hariharan_anantharaman/smartwater/code/smartwater/DataSimulator/target/uber-DataSimulator-0.0.1-SNAPSHOT.jar com.techolution.mauritus.data.simulator.PopulateDa
ta flow 1267 $dataStartTime $dataEndTime -1 3600000