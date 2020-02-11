#pragma once
#include <string>

class DataModel
{
private:
	
	DataModel();

	int rpmStart;
	int rpmEnd;
	int currentRPM;

	int currentSpeed;
	int fuelValue;
	int rangeValue;
	int temperatureValue;
	int pressureValue;
	int voltageValue;
	int odometerValue;
	std::string timeValue;

	int fuelDialAngle;
	int rangeDialAngle;
	int temperatureDialAngle;
	int pressureDialAngle;
	
	bool lightTellTaleStatus;
	bool parkingTellTaleStatus;
	bool hazardTellTaleStatus;
	bool leftTurnTellTaleStatus;
	bool rightTurnTellTaleStatus;
	bool vehicleCheckTellTaleStatus;

	
	
	std::string notificationMessage;
	bool bMenuDisplayNeeded;

public:
	static DataModel* getInstance();
	std::string getNotiMessage();
	int getRpmStart();
	int getRpmEnd();
	void setCurrentRpm(int rpm);
	int getCurrentRpm();
	void setCurrentSpeed(int speed);
	int getCurrentSpeed();
	void setlightTellTaleStatus(bool status);
	bool getlightTellTaleStatus();
	void setparkingTellTaleStatus(bool status);
	bool getparkingTellTaleStatus();

	void sethazardTellTaleStatus(bool status);
	bool gethazardTellTaleStatus();

	void setleftTurnTellTaleStatus(bool status);
	bool getleftTurnTellTaleStatus();
	void setrightTurnTellTaleStatus(bool status);
	bool getrightTurnTellTaleStatus();
	void setvehicleCheckTellTaleStatus(bool status);
	bool getvehicleCheckTellTaleStatus();
	bool isMenuDisplayNeeded();
	void setMenuDisplayStatus(bool status);

	
	void setfuelValue(int status);

	int getfuelValue();

	void setrangeValue(int status);

	int getrangeValue();

	void settemperatureValue(int status);

	int gettemperatureValue();

	void setpressureValue(int status);

	int getpressureValue();

	void setvoltageValue(int status);

	int getvoltageValue();

	void setodometerValue(int status);

	int getodometerValue();

	void settimeValue(std::string status);

	std::string gettimeValue();

	void setfuelDialAngle(int status);

	int getfuelDialAngle();

	void setrangeDialAngle(int status);

	int getrangeDialAngle();

	void settemperatureDialAngle(int status);

	int gettemperatureDialAngle();

	void setpressureDialAngle(int status);

	int getpressureDialAngle();



};