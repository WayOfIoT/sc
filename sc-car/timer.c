/*-----------------------------------------------
  名称：定时器库函数
  编写：夏杰
  日期：2017.4.10
  内容：定时器初始化、中断函数
------------------------------------------------*/
#include "sc.h"

// 100us, 1ms, 1s 定时计数器
uchar timer_100us_counter = 0, timer_ms_counter = 0, timer_s_counter = 0;

/*-----------------------------------------------
  TODO：定时器初始化
  参数：
  编写：夏杰
  日期：2017.4.10
------------------------------------------------*/
void timer_init()
{
	// 定时器0,1初始化
	TMOD = 0x11;

	// 定时器0定时初值设定
	TH0 = (TIMER_FULL_T - TIMER0_T) / 256;	//定时100us,,即0.1ms
	TL0 = (TIMER_FULL_T - TIMER0_T) % 256;
	// 定时器1定时初值设定
	TH1 = (TIMER_FULL_T - TIMER1_T) / 256;	//定时200us,,即0.2ms
	TL1 = (TIMER_FULL_T - TIMER1_T) % 256;

	// 使能总中断
	EA = 1;

	// 使能内部中断0,1
	ET0 = 1;
	ET1 = 1;
	
	// 启动定时器0,1(start timer0,timer1)
	TR0 = 1;
	TR1 = 0;	
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
	TH0 = (TIMER_FULL_T - TIMER0_T) / 256;	//定时100us,,即0.1ms
	TL0 = (TIMER_FULL_T - TIMER0_T) % 256;

	// 定时器计数器累加, timer_100us_counter
	timer_100us_counter++;
	// 1ms定时
	if(timer_100us_counter >= 10)
	{
		timer_100us_counter = 0;
		timer_ms_counter++;
		// 200ms 定时
		if((timer_ms_counter+1) % 200 == 0)
		{
			timer_ms_counter = 0;
			// 液晶刷新标识位
			led_refresh_flag = 1;
			timer_s_counter++;
			if(timer_s_counter >= 250)
			{
				timer_s_counter = 0;
			}	
		}
		// 定时 1s

	}

	/*---------------- 舵机控制 -----------------*/
	// 舵机计数器累加
	steer_pwm_counter++;
	// 舵机PWM波周期控制
	if(steer_pwm_counter == steer_pwm_t)
	{
		steer_pwm_counter = 0;
	}
	// 高电平占空
	if(steer_pwm_counter <= steer_duty)	//PWM周期
		steer_out = 1;
	else
		steer_out = 0;	
		

	// start timer0
	TR0 = 1;
}



void timer1() interrupt 3 using 2
{
	// pause timer1
//	TR1 = 0;

	// 定时器1定时值重置
	TH1 = (TIMER_FULL_T - TIMER1_T) / 256;	//定时100us,,即0.1ms
	TL1 = (TIMER_FULL_T - TIMER1_T) % 256; 

	// 电机PWM波计数器
	motor_pwm_counter++;
	// 电机控制
	motor_control(motor_left_dir_back, 10, motor_right_dir_forward, 10);

	// start timer1
//	TR1 = 1;
}

























