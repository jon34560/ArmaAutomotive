#pragma once
# include "ObjectRendererBase.h"
#include "meter_polygenerator.h"

class RpmRenderer : public ObjectRendererBase
{
private:
	unsigned int rpmMeterFrame;
	c_meter_polygenerator *m_ply_temp;// (306.0f, 460.0f, 80.0f, 250.0f, 800, 1280);
	float angle;
	bool reverse;
	int displayMode = 0;
public:
	RpmRenderer();
	~RpmRenderer();
	 void initialize();
	 void doRender();
	 void dialA();
	 void dialB();
	 void keyPressed(int key);

	 void drawBackground();
	 void drawFilledSun();
	 void DrawCircle(float cx, float cy, float r, int num_segments);
	 void DrawArc(float cx, float cy, float r, int num_segments, int arc_angle);
	 void DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle);
	 void setDisplayMode(int mode);
};