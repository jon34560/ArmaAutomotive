#pragma once

# include "ObjectRendererBase.h"

class ClusterBackgroundRenderer : public ObjectRendererBase
{
private:
	unsigned int bckgroundTexture;
public:
	ClusterBackgroundRenderer();
	~ClusterBackgroundRenderer();
	void initialize();
	void doRender();
	void keyPressed(int key);
};