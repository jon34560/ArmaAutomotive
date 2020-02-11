#pragma once
# include "ObjectRendererBase.h"
#include "meter_polygenerator.h"

class SubDials : public ObjectRendererBase
{
private:
	unsigned int rpmMeterFrame;
	c_meter_polygenerator *m_ply_temp;// (306.0f, 460.0f, 80.0f, 250.0f, 800, 1280);
	float angle;
public:
	SubDials();
	~SubDials();
	void initialize();
	void doRender();
	void drawFuelDial();
	void drawRangeDial();
	void drawTempDial();
	void drawOilPresDial();
	void keyPressed(int key);
};