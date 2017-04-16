#ifndef _STEER_H_
#define _STEER_H_

// 舵机信号输出端
sbit steer_out = P1^0;

// 舵机PWM波周期、计数器
extern unsigned char steer_pwm_t, steer_pwm_counter;

// 舵机PWM占空比,范围:5/200~25/200;小车可用范围 12/200~18/200 
extern unsigned char steer_left_duty, steer_right_duty, steer_middle_duty, steer_duty;


// 舵机转向协议
typedef enum _STEER_DIR_
{
	// 舵机左转向
	steer_dir_left,
	// 舵机正向
	steer_dir_middle,
	// 舵机右转向
	steer_dir_right
} STEER_DIR;

/*-----------------------------------------------
  TODO：舵机初始化
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_init();

/*-----------------------------------------------
  TODO：舵机转向控制
  参数：
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_control(STEER_DIR steer_dir);

/*-----------------------------------------------
  TODO：舵机左转向PWM波占空比
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_left();

/*-----------------------------------------------
  TODO：舵机右转向PWM波占空比
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_right();

/*-----------------------------------------------
  TODO：舵机正向PWM波占空比
  参数：null
  编写：夏杰
  日期：2017.4.12
------------------------------------------------*/
void steer_middle();


#endif




