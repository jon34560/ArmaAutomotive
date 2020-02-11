#include "ClusterDisplayManager.h"

void ClusterDisplayManager::initialize()
{
	rpmRenderObj = new RpmRenderer();
	clusterBackgroundObj = new ClusterBackgroundRenderer();
	clusterSubDialsObj = new SubDials();
	tellTailesObj = new TellTailesRenderer();
	textOverlayObj = new TextOverlayRenderer();
	speedDisplayObj = new SpeedDisplay();

	rpmRenderObj->initialize();
	clusterBackgroundObj->initialize();
	clusterSubDialsObj->initialize();
	tellTailesObj->initialize();
	textOverlayObj->initialize();
	speedDisplayObj->initialize();
}

void ClusterDisplayManager::doRender()
{
	clusterBackgroundObj->doRender();
	rpmRenderObj->doRender();
	clusterSubDialsObj->doRender();
	tellTailesObj->doRender();
	textOverlayObj->doRender();
	speedDisplayObj->doRender();

	
}

void ClusterDisplayManager::keyPressed(int key)
{
	textOverlayObj->keyPressed(key);
}