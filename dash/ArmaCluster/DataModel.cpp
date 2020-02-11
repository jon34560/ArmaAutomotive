#include "DataModel.h"

static DataModel* instance = 0;

DataModel::DataModel()
{
	
	notificationMessage = "No message received!";
	rpmStart = 0;
	rpmEnd = 360;
	currentRPM = 0;
	currentSpeed = 0;
	lightTellTaleStatus = false;
	parkingTellTaleStatus = false;
	hazardTellTaleStatus = false;
	leftTurnTellTaleStatus = false;
	rightTurnTellTaleStatus = false;
	vehicleCheckTellTaleStatus = false;
	bMenuDisplayNeeded = false;
	fuelValue = 20;
	rangeValue = 200;
	temperatureValue = 84;
	pressureValue = 54;
	voltageValue = 14;
	odometerValue = 45687;
	timeValue = "11 : 00 AM";

	fuelDialAngle = 90;
	rangeDialAngle = 180;
	temperatureDialAngle = 270;
	pressureDialAngle = 360;
}

DataModel* DataModel::getInstance()
{
	if ( 0 == instance)
		instance = new DataModel();
	return instance;
}

void DataModel::setlightTellTaleStatus(bool status)
{
	lightTellTaleStatus = status;
}

bool DataModel::getlightTellTaleStatus()
{
	return lightTellTaleStatus;
}

void DataModel::setparkingTellTaleStatus(bool status) // ???
{
	parkingTellTaleStatus = status;
}
bool DataModel::getparkingTellTaleStatus() // ??? 
{
	return parkingTellTaleStatus;
}

void DataModel::sethazardTellTaleStatus(bool status)
{
	hazardTellTaleStatus = status;
}
bool DataModel::gethazardTellTaleStatus() 
{
	return hazardTellTaleStatus;
}

void DataModel::setrightTurnTellTaleStatus(bool status)
{
	rightTurnTellTaleStatus = status;
}
bool DataModel::getrightTurnTellTaleStatus()
{
	return rightTurnTellTaleStatus;
}

void DataModel::setleftTurnTellTaleStatus(bool status)
{
	leftTurnTellTaleStatus = status;
}

bool DataModel::getleftTurnTellTaleStatus()
{
	return leftTurnTellTaleStatus;
}

void DataModel::setvehicleCheckTellTaleStatus(bool status)
{
	vehicleCheckTellTaleStatus = status;
}

bool DataModel::getvehicleCheckTellTaleStatus()
{
	return vehicleCheckTellTaleStatus;
}

bool DataModel::isMenuDisplayNeeded()
{
	return bMenuDisplayNeeded;
}

void DataModel::setMenuDisplayStatus(bool status)
{
	bMenuDisplayNeeded = status;
}
////////////////////////////////////////////////
void DataModel::setfuelValue(int status)
{
	fuelValue = status;
}
int DataModel::getfuelValue()
{
	return fuelValue;
}
void DataModel::setrangeValue(int status)
{
	rangeValue = status;
}
int DataModel::getrangeValue()
{
	return rangeValue;
}
void DataModel::settemperatureValue(int status)
{
	temperatureValue = status;
}
int DataModel::gettemperatureValue()
{
	return temperatureValue;
}
void DataModel::setpressureValue(int status)
{
	pressureValue = status;
}
int DataModel::getpressureValue()
{
	return pressureValue;
}
void DataModel::setvoltageValue(int status)
{
	voltageValue = status;
}
int DataModel::getvoltageValue()
{
	return voltageValue;
}
void DataModel::setodometerValue(int status)
{
	odometerValue = status;
}
int DataModel::getodometerValue()
{
	return odometerValue;
}
void DataModel::settimeValue(std::string status)
{
	timeValue = status;
}
std::string DataModel::gettimeValue()
{
	return timeValue;
}
void DataModel::setfuelDialAngle(int status)
{
	fuelDialAngle = status;
}
int DataModel::getfuelDialAngle()
{
	return fuelDialAngle;
}
void DataModel::setrangeDialAngle(int status)
{
	rangeDialAngle = status;
}
int DataModel::getrangeDialAngle()
{
	return rangeDialAngle;
}
void DataModel::settemperatureDialAngle(int status)
{
	temperatureDialAngle = status;
}
int DataModel::gettemperatureDialAngle()
{
	return temperatureDialAngle;
}
void DataModel::setpressureDialAngle(int status)
{
	pressureDialAngle = status;
}
int DataModel::getpressureDialAngle()
{
	return pressureDialAngle;
}
