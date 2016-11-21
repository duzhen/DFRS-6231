TEST
1-start corba running environment: arguments -ORBInitialPort 1050 -ORBInitialHost localhost
2-run ServerCluster1,2,3,4
3-run ReplicaManager1,2,3,4
4-run Sequencer
5-run Frontendserver
6-run Manager/Passenger

TestRMServer can test RM directly

DEMO
1-start corba running environment: arguments -ORBInitialPort 1050 -ORBInitialHost <hostname>
2-fill the real host name in Config and set TEST false
3-run ServerCluster1,2,3,4
4-run ReplicaManager1,2,3,4
5-run Sequencer
6-run Frontendserver
7-run Manager/Passenger

Function

Client: Manager,Passenger

FrontEnd(FE):

Sequencer(SE):

ReplicaManager(RM): 
	receive FE check error,crash;
	receive HeartBeat;
	receive SE process by CM;
	receive other RM for recovering and correct
	manage server state
	demo
	
ClusterManager(CM):
	manage corba client
	manage log
	correct single failure
	
ServerCluster:
	create,start,restart Corba Server
	receive RM command
	
ServerImpl:
	implementation of Corba interface
Test: