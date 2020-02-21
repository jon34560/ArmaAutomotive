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
	if (key == 'z') {	// Change RPM dial display mode
		rpmDisplayMode++;
		if (rpmDisplayMode == 2) {
			rpmDisplayMode = 0;
		}
		rpmRenderObj->setDisplayMode(rpmDisplayMode);
	}
	else if(key == 'x'){
		clusterDisplayMode++;
		if (clusterDisplayMode == 2) {
			clusterDisplayMode = 0;
		}
		clusterSubDialsObj->setDisplayMode(clusterDisplayMode);
		textOverlayObj->setDisplayMode(clusterDisplayMode);
	}
	else {
		textOverlayObj->keyPressed(key);
	}
}