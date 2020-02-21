#include "SubDials.h"
#include "stb_image.h"
#include "DataModel.h"


SubDials::SubDials()
{
	angle = 0;
	reverse = false;
}

SubDials::~SubDials()
{

}

void SubDials::initialize()
{
	m_ply_temp = new c_meter_polygenerator(200.0f, 200.0f, 200.0f, 270.0f, 400, 400);

	int width, height, nrChannels;
	unsigned char *data = stbi_load(".\\images\\circle.png", &width, &height, &nrChannels, 0);

	glGenTextures(1, &rpmMeterFrame);

	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
	}
	else
	{
		// ToDO : Handle error
	}
	stbi_image_free(data);
	glEnable(GL_TEXTURE_2D);
}

void SubDials::DrawCircle(float cx, float cy, float r, int num_segments) {
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINE_LOOP);
	//glBegin(GL_LINES);
	//glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	for (int ii = 0; ii < num_segments; ii++) {
		float theta = 2.0f * 3.1415926f * float(ii) / float(num_segments); //get the current angle 
		float x = r * cosf(theta); //calculate the x component 
		float y = r * sinf(theta); //calculate the y component 
		glVertex2f(x + cx, y + cy); //output vertex 
	}
	glEnd();
}

/**
* DrawArc
* Description:
*
*/
void SubDials::DrawArc(float cx, float cy, float r, int num_segments, int arc_angle) {
	int arc = (int)((float)num_segments * ((float)arc_angle / (float)360));
	int quarter_segments = (int)((float)num_segments * 0.25);
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINES);
	float prevX = 0;
	float prevY = 0;
	for (int ii = 0; ii < num_segments && ii < arc; ii++) { // - (num_segments/4)
		float theta = (2.0f * 3.1415926f) * ((float(ii + quarter_segments) / float(num_segments))); //
		theta = -theta;
		float x = r * cosf(theta); //calculate the x component 
		float y = r * sinf(theta); //calculate the y component 
		glVertex2f(x + cx, y + cy); //output vertex 

		if (prevX != 0 && prevY != 0) {
			glVertex2f(prevX + cx, prevY + cy);
		}
		prevX = x;
		prevY = y;
	}
	glEnd();
}


void SubDials::DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle) {
	int start = (int)((float)num_segments * ((float)start_angle / (float)360));
	int arc = (int)((float)num_segments * ((float)end_angle / (float)360));
	int quarter_segments = (int)((float)num_segments * 0.25);
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINES);
	float prevX = 0;
	float prevY = 0;
	for (int ii = start; ii < num_segments && ii < arc; ii++) { // - (num_segments/4)
		float theta = (2.0f * 3.1415926f) * ((float(ii + quarter_segments) / float(num_segments))); //
		theta = -theta;
		float x = r * cosf(theta); //calculate the x component 
		float y = r * sinf(theta); //calculate the y component 
		glVertex2f(x + cx, y + cy); //output vertex 

		if (prevX != 0 && prevY != 0) {
			glVertex2f(prevX + cx, prevY + cy);
		}
		prevX = x;
		prevY = y;
	}
	glEnd();
}

void SubDials::DrawRectangle(float x, float y, float width, float height) {
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINE_LOOP);
	//glBegin(GL_LINES);
	//glColor3f(200.0 / 255.0, 200.0 / 255.0, 200.0 / 255.0);
	glVertex2f(x, y);
	glVertex2f(x + width, y);
	glVertex2f(x + width, y + height);
	glVertex2f(x, y + height);
	glVertex2f(x, y);
	
	glEnd();
}

void SubDials::FillRectangle(float x, float y, float width, float height) {
	glBegin(GL_POLYGON);	
	glVertex2f(x, y);
	glVertex2f(x + width, y);
	glVertex2f(x + width, y + height);
	glVertex2f(x, y + height);
	glEnd();
}

void SubDials::doRender()
{
	if (displayMode == 1) {
		DrawSubDialsA();
	}
	else {
		DrawSubDialsB();
	}

	//DrawLeftBox();
}


void SubDials::DrawLeftBox() {
	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(30, 77 - 20, 584, 584);

	// Menu
	glColor3f(90.0 / 255.0, 90.0 / 255.0, 90.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(-0.99, 0.3 + .045, 1.97, 0.5 - 0.045); // border


	// Status Display
	glColor3f(90.0 / 255.0, 90.0 / 255.0, 90.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(-0.99, -0.8, 1.97, 1.1); // border

	glPopMatrix();
	glColor3f(1.0, 1.0, 1.0);
}

void SubDials::DrawSubDialsA(){
	//drawFuelDial();
	//drawRangeDial();
	//drawTempDial();
	//drawOilPresDial();


	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(1280, 77 - 20, 584, 584);
	//drawBackground();

	float radius = 0.28;
	float xPos = -0.45;
	float yPos = 0.45;

	
	// Fuel 
	// Inner circle
	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.5); // red region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 0, 10);
	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 11, 30);
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 31, 360);
	// Draw gauge
	glLineWidth(12.0);
	if (angle < 11) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0);   }		// Red
	else if (angle < 30) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 30) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); } // White
	DrawArc(xPos, yPos, radius, 360, angle);
	// Inner shadow
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); } // White
	DrawArc(xPos, yPos, radius - 0.007, 360, angle);
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); } // White
	DrawArc(xPos, yPos, radius - 0.013, 360, angle);
	// Outer shadow
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); } // White
	DrawArc(xPos, yPos, radius + 0.007, 360, angle);
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); } // White
	DrawArc(xPos, yPos, radius + 0.013, 360, angle);


	// Range 
	xPos = 0.45;
	yPos = 0.45;
	// Inner circle
	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.5); // red region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 0, 10);
	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 11, 30);
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 31, 360);
	// Draw gauge
	glLineWidth(12.0);
	if (angle < 11) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }		// Red
	else if (angle < 30) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 30) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); } // White
	DrawArc(xPos, yPos, radius, 360, angle);
	// Inner shadow
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); } // White
	DrawArc(xPos, yPos, radius - 0.007, 360, angle);
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); } // White
	DrawArc(xPos, yPos, radius - 0.013, 360, angle);
	// Outer shadow
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); } // White
	DrawArc(xPos, yPos, radius + 0.007, 360, angle);
	if (angle < 11) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }		// Red
	else if (angle < 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 30) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); } // White
	DrawArc(xPos, yPos, radius + 0.013, 360, angle);


	// Temp 
	xPos = -0.45;
	yPos = -0.45;
	// Inner circle
	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255, 0.5);
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 0, 45);
	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 46, 90);
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 90, 270);
	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 271, 315);
	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255, 0.5);
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 316, 360);

	// Draw gauge
	glLineWidth(12.0);
	if (angle < 45) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 90) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 270) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); }		// Red
	else if (angle < 315) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 315) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); } // White
	DrawArc(xPos, yPos, radius, 360, angle);
	// Inner shadow
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 360) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); } // White
	DrawArc(xPos, yPos, radius - 0.007, 360, angle);
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); } // White
	DrawArc(xPos, yPos, radius - 0.013, 360, angle);
	// Outer shadow
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); } // White
	DrawArc(xPos, yPos, radius + 0.007, 360, angle);
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); } // White
	DrawArc(xPos, yPos, radius + 0.013, 360, angle);


	// Pressure 
	xPos = 0.45;
	yPos = -0.45;
	// Inner circle

	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255, 0.5);
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 0, 45);
	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 46, 90);
	glColor3f(60.0 / 255.0, 60.0 / 255.0, 60.0 / 255.0);	
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 90, 270);
	glColor4f(225.0 / 255.0, 225.0 / 255.0, 0.0 / 255.0, 0.5); // yellow region
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 271, 315);
	glColor4f(225.0 / 255.0, 0.0 / 255.0, 0.0 / 255, 0.5);
	glLineWidth(2.0);
	DrawArcSegment(xPos, yPos, radius, 360, 316, 360);

	// Draw gauge
	glLineWidth(12.0);
	if (angle < 45) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 90) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 270) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); }		// Red
	else if (angle < 315) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 315) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0);   } // White
	DrawArc(xPos, yPos, radius, 360, angle);
	// Inner shadow
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 360) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17);   } // White
	DrawArc(xPos, yPos, radius - 0.007, 360, angle);
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07);   } // White
	DrawArc(xPos, yPos, radius - 0.013, 360, angle);
	// Outer shadow
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17);   } // White
	DrawArc(xPos, yPos, radius + 0.007, 360, angle);
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.07); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.07); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.07);   } // White
	DrawArc(xPos, yPos, radius + 0.013, 360, angle);


	glPopMatrix();

	glColor3f(1.0, 1.0, 1.0);


	// Draw RPM Dial

	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(667, 77, 584, 584);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	
	if (reverse == false) {
		angle += 2;
	}
	else {
		angle -= 2;
	}
	if (angle > 360) {
		reverse = true;
		//angle = 0;
	}
	if (angle <= 0) {
		reverse = false;
	}
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

/**
* DrawSubDialsB
*
* Bars
*/
void SubDials::DrawSubDialsB() {
	glPushMatrix();
	//glViewport(0, 0, 1920, 720);
	glViewport(1280, 77 - 20, 584, 584);
	//drawBackground();

	float radius = 0.28;
	float xPos = -0.36;
	float yPos = 0.30;


	// Fuel 
	// Border
	glColor3f(90.0 / 255.0, 90.0 / 255.0, 90.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(xPos, yPos, 1.1, 0.1); // border

	// Fill
	if (angle < 22) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }		// Red
	else if (angle < 45) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 45) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); } // White
	FillRectangle(xPos, yPos, (1.1 * (angle / 360)), 0.1);

	// Shadow
	if (angle < 22) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.15); }		// Red
	else if (angle < 45) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.15); }	// Yellow
	else if (angle > 45) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.15); } // White
	float b = 0.009;
	FillRectangle(xPos - b, yPos - b, (1.1 * (angle / 360)) + (b * 2), 0.1 + (b * 2));

	


	// Range 
	xPos = -0.36;
	yPos = 0.1;
	glColor3f(90.0 / 255.0, 90.0 / 255.0, 90.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(xPos, yPos, 1.1, 0.1); // border
	// Fill
	if (angle < 22) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }		// Red
	else if (angle < 45) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 45) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); } // White
	FillRectangle(xPos, yPos, (1.1 * (angle / 360)), 0.1);

	// Shadow
	if (angle < 22) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.15); }		// Red
	else if (angle < 45) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.15); }	// Yellow
	else if (angle > 45) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.15); } // White
	b = 0.009;
	FillRectangle(xPos - b, yPos - b, (1.1 * (angle / 360)) + (b * 2), 0.1 + (b * 2));

	


	// Temperature 
	xPos = -0.36;
	yPos = -0.1;
	glColor3f(90.0 / 255.0, 90.0 / 255.0, 90.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(xPos, yPos, 1.1, 0.1); // border
	if (angle < 45) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 90) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 270) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); }		// Red
	else if (angle < 315) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 315) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); } // White
	float centreX = xPos + ((1.1) / 2);
	FillRectangle(centreX - 0.01, yPos, 0.02, 0.1);
	if (angle < 180) {
		FillRectangle(centreX, yPos, -(1.1 / 2) + (1.1 * ((angle / 360))), 0.1);
	}
	else {
		FillRectangle(centreX, yPos, (1.1 * (angle / 360)) - (1.1 / 2), 0.1); 
	}
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); } // White
	b = 0.009;
	FillRectangle(centreX - 0.01 - b, yPos - b, 0.02 , 0.1 + (b*2));
	if (angle < 180) {
		FillRectangle(centreX - b + 0.03, yPos - b, -(1.1 / 2) + (1.1 * ((angle / 360))) - 0.03, 0.1 + (b * 2));
	}
	else {
		FillRectangle(centreX - b, yPos - b, (1.1 * (angle / 360)) - (1.1 / 2) + (b * 2), 0.1 + (b * 2));
	}
	
	


	// Pressure 
	xPos = -0.36;
	yPos = -0.3;
	
	glColor3f(90.0 / 255.0, 90.0 / 255.0, 90.0 / 255.0);	// White region
	glLineWidth(2.0);
	DrawRectangle(xPos, yPos, 1.1, 0.1); // border
	if (angle < 45) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 90) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }
	else if (angle < 270) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0); }		// Red
	else if (angle < 315) { glColor3f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0); }	// Yellow
	else if (angle > 315) { glColor3f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0); } // White
	centreX = xPos + ((1.1) / 2);
	FillRectangle(centreX - 0.01, yPos, 0.02, 0.1);
	if (angle < 180) {
		FillRectangle(centreX, yPos, -(1.1 / 2) + (1.1 * ((angle / 360))), 0.1);
	}
	else {
		FillRectangle(centreX, yPos, (1.1 * (angle / 360)) - (1.1 / 2), 0.1);
	}
	if (angle < 45) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 90) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }
	else if (angle < 270) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 255.0 / 255.0, 0.17); }		// Red
	else if (angle < 315) { glColor4f(255.0 / 255.0, 255.0 / 255.0, 0.0 / 255.0, 0.17); }	// Yellow
	else if (angle > 315) { glColor4f(255.0 / 255.0, 0.0 / 255.0, 0.0 / 255.0, 0.17); } // White
	b = 0.009;
	FillRectangle(centreX - 0.01 - b, yPos - b, 0.02, 0.1 + (b * 2));
	if (angle < 180) {
		FillRectangle(centreX - b + 0.03, yPos - b, -(1.1 / 2) + (1.1 * ((angle / 360))) - 0.03, 0.1 + (b * 2));
	}
	else {
		FillRectangle(centreX - b, yPos - b, (1.1 * (angle / 360)) - (1.1 / 2) + (b * 2), 0.1 + (b * 2));
	}
	



	glPopMatrix();

	glColor3f(1.0, 1.0, 1.0);


	// Draw RPM Dial

	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(667, 77, 584, 584);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	if (reverse == false) {
		angle += 2;
	}
	else {
		angle -= 2;
	}
	if (angle > 360) {
		reverse = true;
		//angle = 0;
	}
	if (angle <= 0) {
		reverse = false;
	}
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

/**
* drawFuelDial
*
*/ 
void SubDials::drawFuelDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1341, 397, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int fuelDialVal = DataModel::getInstance()->getfuelDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * fuelDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(216.0/255.0, 34.0/256.0, 0.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::drawRangeDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1593, 397, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int rangeDialVal = DataModel::getInstance()->getrangeDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * rangeDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(12.0 / 256.0, 218.0 / 256.0, 0.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::drawTempDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1341, 152, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int temperatureDialVal = DataModel::getInstance()->gettemperatureDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * temperatureDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(216.0 / 255.0, 34.0 / 256.0, 0.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::drawOilPresDial()
{
	glPushMatrix();
	glEnable(GL_TEXTURE_2D);
	glViewport(1593, 152, 189, 189);

	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, rpmMeterFrame);

	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	int pressureDialVal = DataModel::getInstance()->getpressureDialAngle();
	int s32_count = 0;
	std::vector<Triangle> TrianglesVe;
	TrianglesVe = m_ply_temp->get_traignles_for_rendering(-1 * pressureDialVal);

	for (s32_count = 0; s32_count < TrianglesVe.size(); s32_count++)
	{
		glBegin(GL_TRIANGLES);
		glColor3f(254.0 / 255.0, 255.0 / 256.0, 14.0 / 256.0);

		glTexCoord2f((TrianglesVe[s32_count].uv1[0]), 1.0 - TrianglesVe[s32_count].uv1[1]);
		glVertex2f(TrianglesVe[s32_count].point1[0], TrianglesVe[s32_count].point1[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv3[0]), 1.0 - TrianglesVe[s32_count].uv3[1]);
		glVertex2f((TrianglesVe[s32_count].point3[0]), TrianglesVe[s32_count].point3[1]);

		glTexCoord2f((TrianglesVe[s32_count].uv2[0]), 1.0 - TrianglesVe[s32_count].uv2[1]);
		glVertex2f((TrianglesVe[s32_count].point2[0]), TrianglesVe[s32_count].point2[1]);

		glEnd();
	}
	glColor3f(1.0, 1.0, 1.0);
	angle++;
	if (angle > 360)
		angle = 0;
	glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void SubDials::keyPressed(int key)
{

}

void SubDials::setDisplayMode(int mode) {
	this->displayMode = mode;
}