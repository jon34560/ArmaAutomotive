#pragma once
#include "RpmRenderer.h"
#include "ClusterBackgroundRenderer.h"
#include "SubDials.h"
#include "TellTalesRenderer.h"
#include "TextOverlayRenderer.h"
#include "SpeedDisplay.h"
#include "MenuRenderer.h"
#include "ObjectRendererBase.h"

class ClusterDisplayManager
{
private:
	int rpmDisplayMode = 0;
	int clusterDisplayMode = 0;
public:
	ObjectRendererBase *rpmRenderObj;
	ObjectRendererBase *clusterBackgroundObj;
	ObjectRendererBase *clusterSubDialsObj;
	ObjectRendererBase *tellTailesObj;
	ObjectRendererBase *textOverlayObj;
	ObjectRendererBase *speedDisplayObj;
	void initialize();
	void doRender();
	void keyPressed(int key);
};