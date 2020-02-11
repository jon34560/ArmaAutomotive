/**
* Arma Automotive 
*
* Date: February 2, 2020
* 
* Note: Building under Visual Studio 2019 16.4.2 -> Set Debug Information Format to None (/FS)
* 
* License: GPL
*/

#include <iostream>
#include <string>
#include <GL/glew.h>
#include <GL/freeglut.h>
#include "DataModel.h"
#include <mutex>
#include <thread>
#include <chrono>

#include "ClusterDisplayManager.h"
//#include "DataLogger.h"
#include "rs232.h"

#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif


/* Handler for window-repaint event. Call back when the window first appears and
whenever the window needs to be re-painted. */
ClusterDisplayManager clusterDispMgr;
int WINDOW_WIDTH = 1920;
int WINDOW_HEIGHT = 720;
int nCount = 0;
int nMenuDisplayCounter = 0;

// Thread
void dataAcquisitionThread() { // pass in state (DataModel dataModel)
	int counter = 0;

	// Probe serial ports for devices.
	int i, n,
		cport_nr = 3,        /* /dev/ttyS0 (COM1 on windows) */
		bdrate = 57600;      /* baud */

	unsigned char buf[4096];

	char mode[] = {'8', 'N', '1', 0}; // [5|6|7|8] Bits, [N|E|O] Parity, [1,2] Stop Bits
	if (RS232_OpenComport(cport_nr, bdrate, mode, 0))
	{
		printf("Can not open comport\n");
		//return(0);
	} 
	/*
	while(1)
	  {
		n = RS232_PollComport(cport_nr, buf, 4095);

		if(n > 0)
		{
		  buf[n] = 0;   // always put a "null" at the end of a string! 

		for (i = 0; i < n; i++)
		{
			if (buf[i] < 32)  // replace unreadable control-codes by dots 
			{
				buf[i] = '.';
			}
		}

		printf("received %i bytes: %s\n", n, (char*)buf);
	}
	*/



	while (true) {
	
		DataModel::getInstance()->setrightTurnTellTaleStatus(!DataModel::getInstance()->getrightTurnTellTaleStatus());
		DataModel::getInstance()->setleftTurnTellTaleStatus(!DataModel::getInstance()->getleftTurnTellTaleStatus());

		DataModel::getInstance()->sethazardTellTaleStatus(!DataModel::getInstance()->gethazardTellTaleStatus()); 
		DataModel::getInstance()->setvehicleCheckTellTaleStatus(!DataModel::getInstance()->getvehicleCheckTellTaleStatus());

		DataModel::getInstance()->setparkingTellTaleStatus(!DataModel::getInstance()->getparkingTellTaleStatus());
		DataModel::getInstance()->setlightTellTaleStatus(!DataModel::getInstance()->getlightTellTaleStatus());

		
		 
		int fuel = DataModel::getInstance()->getfuelValue();
		fuel++;
		if (fuel > 100) {
			fuel = 0;
		}
		DataModel::getInstance()->setfuelValue(fuel);
		DataModel::getInstance()->setfuelDialAngle(fuel);
		//setfuelDialAngle  getfuelDialAngle


		std::this_thread::sleep_for(std::chrono::milliseconds(500));
		counter++;
	}
}

void display() 
{
	glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
	glClear(GL_COLOR_BUFFER_BIT);         // Clear the color buffer (background)

	clusterDispMgr.doRender();
	//glFlush();
	glutSwapBuffers();
	Sleep(50);
	nCount++;
	//if (nCount % 15 == 0)
	//{
		//DataModel::getInstance()->setrightTurnTellTaleStatus(!DataModel::getInstance()->getrightTurnTellTaleStatus()); // testing only
		//DataModel::getInstance()->setleftTurnTellTaleStatus(!DataModel::getInstance()->getleftTurnTellTaleStatus());
	//}

	if (nMenuDisplayCounter > 250)
		DataModel::getInstance()->setMenuDisplayStatus(false);
	nMenuDisplayCounter++;
	
	glutSwapBuffers();
	glutPostRedisplay();
}





void keyboard_up(unsigned char key, int x, int y)
{
	switch (key) {
	case 13: 
		exit(0);
		break;
	case VK_LEFT:

		break;
	case VK_RIGHT:

		break;
	case VK_UP:
		exit(0);
	default:
		break;
	}

}

void SpecialKeys(int key, int x, int y)
{
	nMenuDisplayCounter = 0;
	clusterDispMgr.keyPressed(key);
}

/* Main function: GLUT runs as a console application starting at main()  */
int main(int argc, char** argv) {
	glutInit(&argc, argv);					// Initialize GLUT

	glutCreateWindow("Arma Automotive Dash");	// Create a window with the given title
	glutInitWindowSize(500, 500);			// Set the window's initial width & height
	glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE);
	glutInitWindowPosition(0, 0);			// Position the window's initial top-left corner
	glutDisplayFunc(display);				// Register display callback handler for window re-paint
	glutKeyboardUpFunc(keyboard_up);		// when the key goes up
	glutSpecialUpFunc(SpecialKeys);

	glutReshapeWindow(WINDOW_WIDTH, WINDOW_HEIGHT);

	clusterDispMgr.initialize();

	// Start data acquisition thread
	std::thread t(dataAcquisitionThread);

	glutMainLoop();           // Enter the event-processing loop
	return 0;
}