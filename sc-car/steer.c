/*-----------------------------------------------
  名称：舵机转向驱动函数库
  编写：夏杰
  日期：2017.4.12
  内容：舵机控制相关函数
------------------------------------------------*/

#include "sc.h"

// 舵机PWM波周期、计数器
uint steer_pwm_t = 200, steer_pwm_counter = 0;

// 舵机PWM占空比,范围:5/200~25/200 
uchar steer_left_duty = 18, steer_right_duty = 12, steer_middle_duty = 15, steer_duty;

/*-----------------------------------------------
  TODO：舵机初始化
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_init()
{
	// 舵机计数器置位
	steer_pwm_counter = 0;	
	// 舵机初始位置置中
	steer_duty = steer_middle;
}

/*-----------------------------------------------
  TODO：舵机转向控制
  参数：
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_control(STEER_DIR steer_dir)
{
	// 舵机PWM波周期控制
	if(steer_pwm_counter >= steer_pwm_t)
	{
		steer_pwm_counter = 0;
	}
	// 舵机转向判断
	switch(steer_dir)
	{
		case steer_dir_left:
			steer_left();
			break;
		case steer_dir_middle:
			steer_middle();
			break;
		case steer_dir_right:
			steer_right();
			break;
	}
	// 高电平占空
	if(steer_pwm_counter <= steer_duty)	//PWM周期
		steer_out = 1;
	else
		steer_out = 0;
}

/*-----------------------------------------------
  TODO：舵机左转向PWM波占空比
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_left()
{
	steer_duty = steer_left_duty;
}

/*-----------------------------------------------
  TODO：舵机右转向PWM波占空比
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_right()
{
	steer_duty = steer_right_duty;
}

/*-----------------------------------------------
  TODO：舵机正向PWM波占空比
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_middle()
{
	steer_duty = steer_middle_duty;
}



