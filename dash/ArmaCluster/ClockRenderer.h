#pragma once
# include "ObjectRendererBase.h"
#include "RpmRenderer.h"

class ClockRenderer : public ObjectRendererBase
{
private:
	float angle;
	bool reverse;
	RpmRenderer rpmRenderer;

public:
	ClockRenderer();
	~ClockRenderer();
	void initialize();
	void doRender();
	void drawTellTale();
	void keyPressed(int key);
	void drawClock();

	//void DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle);
	//void DrawCircle(float cx, float cy, float r, int num_segments);
	//void DrawArc(float cx, float cy, float r, int num_segments, int arc_angle);
	//void DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle);
};