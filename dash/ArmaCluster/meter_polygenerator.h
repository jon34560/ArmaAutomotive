
#pragma once
#include<stdio.h>
#include<stdlib.h>
#include <vector>

struct Triangle
{
	float point1[3];
	float point2[3];
	float point3[3];

	float uv1[2];
	float uv2[2];
	float uv3[2];

	Triangle()
	{

		point1[0] = point1[1] = point1[2] = 0;
		point2[0] = point2[1] = point2[2] = 0;
		point3[0] = point3[1] = point3[2] = 0;

		uv1[0] = uv1[1] = 0;
		uv2[0] = uv2[1] = 0;
		uv3[0] = uv3[1] = 0;
	}
};

class c_meter_polygenerator
{
	float fl32_X,fl32_Y;
	float fl32_outer_radius;
	float fl32_inner_radius;
	float fl32_start_angle;

	float fl32_image_height;
	float fl32_image_width;

public:
	c_meter_polygenerator(float fl32_x,float fl32_y,float fl32_radius,float fl32_st_angle,float fl32_im_height,float fl32_im_Width);
	std::vector<Triangle> get_traignles_for_rendering(float fl32_end_angle);
	float doNormalize(float val);
};

