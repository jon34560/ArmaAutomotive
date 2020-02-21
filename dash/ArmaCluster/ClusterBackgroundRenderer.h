#pragma once
# include "ObjectRendererBase.h"


class ClusterBackgroundRenderer : public ObjectRendererBase
{
private:
	unsigned int bckgroundTexture;
	float x[10];
	float y[10];
	float angle[20];



public:
	ClusterBackgroundRenderer();
	~ClusterBackgroundRenderer();
	void initialize();
	void doRender();
	void keyPressed(int key);
	void DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle);
	void DrawLine(float cx, float cy, float dx, float dy);
};