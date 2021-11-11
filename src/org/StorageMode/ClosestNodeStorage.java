package org.StorageMode;

import java.util.Calendar;

import org.Results.SaveResults;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.application.Application;
import org.fog.examples.DataPlacement;
import org.fog.lpFileConstuction.BasisDelayMatrix;
import org.fog.stats.LatencyStats;
import org.fog.stats.Stats;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.utils.TimeKeeper;
import org.fog2.entities.FogBroker;

public class ClosestNodeStorage {
	
	public ClosestNodeStorage(){
		
	}

	public void sim() throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("----------------------------------------------------------");
		System.out.println(DataPlacement.ClosestNode);
		Log.writeInLogFile("DataPlacement","----------------------------------------------------------");
		Log.writeInLogFile("DataPlacement", DataPlacement.ClosestNode);

		CloudSim.init(DataPlacement.num_user, DataPlacement.calendar, DataPlacement.trace_flag);
		String appId = "Data_Placement"; // identifier of the application
		
		long b_sim, e_sim;
		long begin_t, end_t, period_t;
		
		b_sim = Calendar.getInstance().getTimeInMillis();
		
		FogBroker broker = new FogBroker("broker");
		System.out.println("Creating of the Fog devices!");
		Log.writeInLogFile("DataPlacement","Creating of the Fog devices!");
		DataPlacement.createFogDevices(broker.getId(), appId);

		System.out.println("Creating of Sensors and Actuators!");
		Log.writeInLogFile("DataPlacement","Creating of Sensors and Actuators!");
		DataPlacement.createSensorsAndActuators(broker.getId(), appId);

		/* Module deployment */
		System.out.println("Creating and Adding modules to devices");
		Log.writeInLogFile("DataPlacement","Creating and Adding modules to devices");
		ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
		moduleMapping.addModulesToFogDevices();
		moduleMapping.setModuleToHostMap();
		Application application = DataPlacement.createApplication(appId, broker.getId());
		application.setUserId(broker.getId());
		application.setFogDeviceList(DataPlacement.fogDevices);

		System.out.println("Controller!");
		Log.writeInLogFile("DataPlacement", "Controller!");
		Controller controller = new Controller("master-controller",DataPlacement.fogDevices, DataPlacement.sensors, DataPlacement.actuators, moduleMapping);
		controller.submitApplication(application, 0);
		
		e_sim = Calendar.getInstance().getTimeInMillis();
		org.fog.examples.Log.writeInfraCreationTime(DataPlacement.nb_HGW, String.valueOf((e_sim - b_sim)));
				

		TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

		begin_t = Calendar.getInstance().getTimeInMillis();
		System.out.println("Basis Delay Matrix computing!");
		Log.writeInLogFile("DataPlacement","Basis Delay Matrix computing!");
		BasisDelayMatrix delayMatrix = new BasisDelayMatrix(DataPlacement.fogDevices,DataPlacement.nb_Service_HGW, DataPlacement.nb_Service_LPOP, DataPlacement.nb_Service_RPOP,DataPlacement.nb_Service_DC, DataPlacement.nb_HGW, DataPlacement.nb_LPOP, DataPlacement.nb_RPOP, DataPlacement.nb_DC,application);
		delayMatrix.getDelayMatrix(DataPlacement.fogDevices);
		end_t = Calendar.getInstance().getTimeInMillis();
		
		period_t = end_t - begin_t;
		
		org.fog.examples.Log.writeShortPathTime(DataPlacement.nb_HGW,"consProd:"+ DataPlacement.nb_DataCons_By_DataProd + "		storage mode:"+DataPlacement.storageMode+"		short path time (floyd):"+String.valueOf(period_t));


		//BasisDelayMatrix.saveBasisDelayMatrix();
		//BasisDelayMatrix.savemFlowMatrix();
		//BasisDelayMatrix.savemAdjacenceMatrix();
		//GraphPartitionStorage.saveNbNodesAndNbArcs(DelayMatrix_Float.mTotalNodeNum, DelayMatrix_Float.mTotalArcNum);

		/*
		 * Connecting the application modules (vertices) in the
		 * application model (directed graph) with edges
		 */
		System.out.println("Add adges to application");
		application.addAppEdgesToApplication();

		/*
		 * Defining the input-output relationships (represented by
		 * selectivity) of the application modules.
		 */
		System.out.println("Add Tuple Fraction to application");
		application.addTupleMappingFraction();
		
//		System.out.println("Loading ....");
//		application.loadApplicationEdges();
//		application.loadTupleMappingFraction();
//		BasisDelayMatrix.loadBasisDelayMatrix();
//		System.out.println("Loaded");
				
		System.out.println("Start simulation...");
		b_sim = Calendar.getInstance().getTimeInMillis();
		CloudSim.startSimulation();
		CloudSim.stopSimulation();
		e_sim = Calendar.getInstance().getTimeInMillis();
		System.out.println("End of simulation");
		org.fog.examples.Log.writeSimulationTime(DataPlacement.nb_HGW, String.valueOf((e_sim - b_sim)));
		
		System.out.println("End of simulation!");

		System.out.println(DataPlacement.storageMode);
		System.out.println("Read Latency:"+ LatencyStats.getOverall_read_Latency());
		System.out.println("Write Latency:"+ LatencyStats.getOverall_write_Latency());
		System.out.println("Overall Latency:"+ LatencyStats.getOverall_Latency());

		Log.writeInLogFile("DataPlacement", DataPlacement.storageMode);
		Log.writeInLogFile("DataPlacement", "Read Latency:"+ LatencyStats.getOverall_read_Latency());
		Log.writeInLogFile("DataPlacement", "Write Latency:"+ LatencyStats.getOverall_write_Latency());
		Log.writeInLogFile("DataPlacement", "Overall Latency:"+ LatencyStats.getOverall_Latency());

		SaveResults.saveLatencyTimes(DataPlacement.nb_DataCons_By_DataProd, DataPlacement.storageMode,
				LatencyStats.getOverall_read_Latency(),
				LatencyStats.getOverall_write_Latency(),
				LatencyStats.getOverall_delayed_write_Latency(),
				Stats.version_exchange_latency,
				LatencyStats.getOverall_Latency());
		SaveResults.saveAllStats();
		
		LatencyStats.reset_ALLStats();
		org.fog.stats.PenalityStats.reset_Penality_Stats();
		Stats.reset_AllStats();

		System.out.println("VRGame finished!");

		DataPlacement.fogDevices.clear();
		DataPlacement.sensors.clear();
		DataPlacement.actuators.clear();
		System.gc();

		
	}
}
