#include "ClusterBackgroundRenderer.h"
/*#ifndef STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_IMPLEMENTATION
#endif // !STB_IMAGE_IMPLE*/

#include "stb_image.h"
#include <math.h>
#include <string>

ClusterBackgroundRenderer::ClusterBackgroundRenderer()
{
	bckgroundTexture = -1;
	for (int i = 0; i < 10; i++) {
		//x[i] = 0.5 - ((rand() % 100) / 100.0);	// Generate random locations for stars
		//y[i] = 0.5 - ((rand() % 100) / 100.0);
		//float a = atan2(y[i] - 0, x[i] - 0);	// calculate correct angle for given star.
		//angle[i] = a; //  (float)3.60 * (float)i;

		angle[i] = (float)6.2 *  ((float)i / 10) ;
		float distance = ((rand() % 100) / 100.0);
		x[i] = distance * cos(angle[i]);
		y[i] = distance * sin(angle[i]);

		//x[i] = 0;
		//y[i] = 0;
	}
}

ClusterBackgroundRenderer::~ClusterBackgroundRenderer()
{

}

void ClusterBackgroundRenderer::initialize()
{
	int width, height, nrChannels;
	unsigned char *data = stbi_load(".\\images\\bk.jpg", &width, &height, &nrChannels, 0);

	glGenTextures(1, &bckgroundTexture);

	glBindTexture(GL_TEXTURE_2D, bckgroundTexture);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	if (data)
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
	}
	else
	{
		// ToDO : Handle error
	}
	stbi_image_free(data);	
}


/**
*
* Draw background image
*/
void ClusterBackgroundRenderer::doRender()
{
	// glMatrixMode(GL_MODELVIEW);
	// glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	
	glViewport(0, 0, 1920, 720);
	//drawBackground();

	float radius = 0.739;

	// Draw unit line
	for (int i = 0; i < 10; i++) {

		float color = 90 + ((float)255 * (x[i] / 4));
		glColor3f(color / 255.0, color / 255.0, color / 255.0);
		glLineWidth(2.0);
		//DrawArcSegment(x, y, radius, 720, 60, 62);
		
		
		float length = (0.1 * (((x[i] / 2) + 0.02) / 1));
		float xLength = length * cos(angle[i]);
		float yLength = length * sin(angle[i]);

		DrawLine(x[i], y[i], x[i] + xLength, y[i] + yLength);

		float xDist = (0.1 * ((( abs(x[i]) / 2) + 0.02) / 1));
		float yDist = (0.1 * (((abs(y[i]) / 2) + 0.02) / 1));
		float dist = xDist + yDist;

		float xMove = dist * cos(angle[i]);
		//printf( std::to_string(xMove).c_str() );
		x[i] = x[i] + xMove;
		if (x[i] > 1) {
			x[i] = 0;
			y[i] = 0;
		}
		if (x[i] < -1) {
			x[i] = 0;
			y[i] = 0;
		}
		float yMove = dist * sin(angle[i]);
		y[i] = y[i] + yMove;
		if (y[i] > 1) {
			x[i] = 0;
			y[i] = 0;
		}
		if (y[i] < -1) {
			x[i] = 0;
			y[i] = 0;
		}
	}

	//glDisable(GL_TEXTURE_2D);
	glPopMatrix();
}

void ClusterBackgroundRenderer::keyPressed(int key)
{

}

void ClusterBackgroundRenderer::DrawLine(float cx, float cy, float dx, float dy) {
	glEnable(GL_LINE_SMOOTH);
	glBegin(GL_LINES);
	glVertex2f(cx, cy); //output vertex 
	glVertex2f(dx,  dy);
	glEnd();
}

void ClusterBackgroundRenderer::DrawArcSegment(float cx, float cy, float r, int num_segments, int start_angle, int end_angle) {
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