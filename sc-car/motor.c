/*-----------------------------------------------
  名称：电机驱动函数库
  编写：夏杰
  日期：2017.4.13
  内容：
------------------------------------------------*/

#include "sc.h"

// 电机驱动PWM占空比
uint motor_left_duty = 0, motor_right_duty = 0;

// 电机PWM波计数器
uint motor_pwm_counter = 0, motor_pwm_t = 100;

/*-----------------------------------------------
  TODO：电机驱动初始化
  参数：null
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_init()
{
	// 初始化电机停止
	motor_left_stop();
	motor_right_stop();
}

/*-----------------------------------------------
  TODO：电机驱动正反转、占空比控制
  参数：
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_control(MOTOR_DIR motor_left_dir, uint motor_left_duty, MOTOR_DIR motor_right_dir, uint motor_right_duty)
{
	// 电机PWM波周期
	if(motor_pwm_counter >= motor_pwm_t)
	{
		motor_pwm_counter = 0;
	}
	
	// 左电机驱动
	switch(motor_left_dir)	
	{
		case motor_left_dir_forward:
			motor_left_forward(motor_left_duty);
			break;
		case motor_left_dir_back:
			motor_left_back(motor_left_duty);
			break;
		case motor_left_dir_stop:
			motor_left_stop();
			break;
	}

	// 右电机驱动
	switch(motor_right_dir)
	{
		// 右电机正转
		case motor_right_dir_forward:
			motor_right_forward(motor_right_duty);
			break;
		case motor_right_dir_back:
			motor_right_back(motor_right_duty);
			break;
		case motor_right_dir_stop:
			motor_right_stop();
			break;
	}
}

/*-----------------------------------------------
  TODO：左电机正转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_left_forward(uchar motor_duty)
{
	// 电平驱动左电机正转
	motor_left_in = MOTOR_ON;
	motor_left_out = MOTOR_OFF;
	
	// PWM波驱动
	motor_left_duty = motor_duty;
	if(motor_pwm_counter <= motor_left_duty)
	{
		motor_left_in = MOTOR_ON;
	}
	else
	{
		motor_left_in = MOTOR_OFF;
	}
	motor_left_out = MOTOR_OFF;
}

/*-----------------------------------------------
  TODO：左电机反转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_left_back(uchar motor_duty)
{
	motor_left_in = MOTOR_OFF;
	motor_left_out = MOTOR_ON;

	// PWM波驱动
	motor_left_duty = motor_duty;
	if(motor_pwm_counter <= motor_left_duty)
	{
		motor_left_out = MOTOR_ON;
	}
	else
	{
		motor_left_out = MOTOR_OFF;
	}
	motor_left_in = MOTOR_OFF;
}

/*-----------------------------------------------
  TODO：左电机停止
  参数：null
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_left_stop()
{
	// 电平驱动
	motor_left_in = MOTOR_OFF;
	motor_left_out = MOTOR_OFF;

	// PWM波占空比驱动
	motor_left_duty = 0;
	motor_right_duty = 0;
}

/*-----------------------------------------------
  TODO：右电机正转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_right_forward(uchar motor_duty)
{	
	motor_right_in = MOTOR_ON;
	motor_right_out = MOTOR_OFF;

	// PWM波驱动
	motor_right_duty = motor_duty;
	if(motor_pwm_counter <= motor_right_duty)
	{
		motor_right_in = MOTOR_ON;
	}
	else
	{
		motor_right_in = MOTOR_OFF;
	}
	motor_right_out = MOTOR_OFF;
}

/*-----------------------------------------------
  TODO：右电机反转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_right_back(uchar motor_duty)
{
	motor_right_in = MOTOR_OFF;
	motor_right_out = MOTOR_ON;

	// PWM波驱动
	motor_right_duty = motor_duty;
	if(motor_pwm_counter <= motor_right_duty)
	{
		motor_right_out = MOTOR_ON;
	}
	else
	{
		motor_right_out = MOTOR_OFF;
	}
	motor_right_in = MOTOR_OFF;
}

/*-----------------------------------------------
  TODO：右电机停止
  参数：null
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_right_stop()
{
	motor_right_in = MOTOR_OFF;
	motor_right_out = MOTOR_OFF;

	// PWM波占空比驱动
	motor_left_duty = 0;
	motor_right_duty = 0;
}





