#include "ClusterBackgroundRenderer.h"
/*#ifndef STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_IMPLEMENTATION
#endif // !STB_IMAGE_IMPLE*/

#include "stb_image.h"

ClusterBackgroundRenderer::ClusterBackgroundRenderer()
{
	bckgroundTexture = -1;
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
	/*
	glPushMatrix();
	
	glEnable(GL_TEXTURE_2D);
	glBindTexture(GL_TEXTURE_2D, bckgroundTexture);
	glViewport(0, 0, 1920, 720);

	glRotatef(180.0, 1, 0, 0);	// ?
	glBegin(GL_QUADS);

	glTexCoord2f(0.0, 0.0);		// ?
	glVertex2f(-1.0, -1.0);

	glTexCoord2f(0.0, 1.0);
	glVertex2f(-1.0, 1.0);

	glTexCoord2f(1.0, 1.0);
	glVertex2f(1.0,1.0);

	glTexCoord2f(1.0, 0.0);
	glVertex2f(1.0, -1.0);

	glEnd();
	//glFlush();
	glDisable(GL_TEXTURE_2D);

	glPopMatrix();
	*/
}

void ClusterBackgroundRenderer::keyPressed(int key)
{

}