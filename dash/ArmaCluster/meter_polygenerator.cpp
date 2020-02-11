#include <math.h>
#include "meter_polygenerator.h"


c_meter_polygenerator::c_meter_polygenerator(float fl32_x,float fl32_y,float fl32_radius,float fl32_st_angle,float fl32_im_height,float fl32_im_width)
{
	fl32_image_width = fl32_im_width;
	fl32_image_height = fl32_im_height;
	fl32_start_angle = fl32_st_angle;
	fl32_X = fl32_x;
	fl32_Y = fl32_y;
        fl32_outer_radius = fl32_radius;	
}

float c_meter_polygenerator::doNormalize(float val)
{
	float result = -1 + ((2 * val) / 400.0);
	return result;
}

std::vector<Triangle> c_meter_polygenerator::get_traignles_for_rendering(float fl32_end_angle) 
{

	std::vector<Triangle> Triangles;
	float fl32_delta_angle = 0;
	float fl32_start_ang = fl32_start_angle;
	do
	{
                Triangle t;                
		fl32_delta_angle = (::abs(fl32_end_angle)  <= 5 ? fl32_end_angle : 5 * (fl32_end_angle < 0 ? -1 : 1) ) ;
	
		t.point1[0] = fl32_X;
		t.point1[1] = fl32_Y;
		t.point1[2] = 0;

		t.point2[0] =  fl32_outer_radius *  static_cast<float>(cos(fl32_start_ang* 3.14 / 180.0F)) + fl32_X;
		t.point2[1] =  fl32_outer_radius *  static_cast<float>(sin(fl32_start_ang* 3.14 / 180.0F)) + fl32_Y;
		t.point2[2] = 0;

		t.point3[0] = fl32_outer_radius *  static_cast<float>(cos((fl32_start_ang+fl32_delta_angle)* 3.14 / 180.0F)) + fl32_X;
		t.point3[1] = fl32_outer_radius *  static_cast<float>(sin((fl32_start_ang+fl32_delta_angle)* 3.14 / 180.0F)) + fl32_Y;
		t.point3[2] = 0;

		t.point1[0] = doNormalize(t.point1[0]);
		t.point1[1] = doNormalize(t.point1[1]);

		t.uv1[0] = (t.point1[0] + 1.0) / 2.0;
		t.uv1[1] = (t.point1[1] + 1.0) / 2.0;

		t.point2[0] = doNormalize(t.point2[0]);
		t.point2[1] = doNormalize(t.point2[1]);

		t.uv2[0] = (t.point2[0] + 1.0) / 2.0;
		t.uv2[1] = (t.point2[1] + 1.0) / 2.0;

		t.point3[0] = doNormalize(t.point3[0]);
		t.point3[1] = doNormalize(t.point3[1]);

		t.uv3[0] = (t.point3[0] + 1.0) / 2.0;
		t.uv3[1] = (t.point3[1] + 1.0) / 2.0;

		Triangles.push_back(t);

		fl32_start_ang += fl32_delta_angle;

		fl32_end_angle -= fl32_delta_angle;

	}while(abs(fl32_end_angle) > 0 );

	return Triangles;
}
