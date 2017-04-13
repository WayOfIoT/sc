/*-----------------------------------------------
  名称：定时器库函数
  编写：夏杰
  日期：2017.4.10
  内容：定时器初始化、中断函数
------------------------------------------------*/
#include "sc.h"

/*-----------------------------------------------
  TODO：定时器初始化
  参数：
  编写：夏杰
  日期：2017.4.10
------------------------------------------------*/
void timer_init()
{
	// 定时器初始化
	TMOD = 0x01;
	TH0 = (65535 - timer0_t) / 256;	//定时500us,,即0.5ms
	TL0 = (65535 - timer0_t) % 256;

	// 使能总中断
	EA = 1;

	// 使能内部中断0
	ET0 = 1;
	
	// 启动定时器0(start timer0)
	TR0 = 1;	
}

/*-----------------------------------------------
  TODO：定时器0中断函数
  参数：
  编写：夏杰
  日期：2017.4.10
------------------------------------------------*/
void timer0() interrupt 1 using 1
{	
	// pause timer0
	TR0 = 0;

	// 定时器重置
	TH0 = (65535 - timer0_t) / 256;	//定时100us,,即0.1ms
	TL0 = (65535 - timer0_t) % 256;

	// 电机PWM波计数器
	motor_pwm_counter++;
	// 电机控制
	motor_control(motor_left_dir_forward, 100, motor_right_dir_forward, 100);

	// 舵机计数器累加
	steer_pwm_counter++;
	steer_control(steer_dir_middle);	

	// start timer0
	TR0 = 1;
}

























