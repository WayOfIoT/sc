#ifndef _MOTOR_H_
#define _MOTOR_H_

// 电机输出端口
sbit motor_left_in = P1^2;
sbit motor_left_out = P1^3;
sbit motor_right_in = P1^4;
sbit motor_right_out = P1^5;

// 电机驱动电平
#define MOTOR_ON 1
#define MOTOR_OFF 0

// 电机驱动PWM占空比
extern unsigned int motor_left_duty, motor_right_duty;

// 电机PWM波计数器
extern unsigned int motor_pwm_counter;

// 电机正反转参数设置
typedef enum MOTOR_DIR
{
	// 左电机正转
	motor_left_dir_forward,
	// 左电机反转
	motor_left_dir_back,
	// 左电机停止
	motor_left_dir_stop,
	// 右电机正转
	motor_right_dir_forward,
	// 右电机反转
	motor_right_dir_back,
	//  右电机停止
	motor_right_dir_stop	
}MOTOR_DIR;

/*----------- 函数声明 ------------*/

/*-----------------------------------------------
  TODO：电机驱动初始化
  参数：null
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_init();

/*-----------------------------------------------
  TODO：电机驱动正反转、占空比控制
  参数：
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_control(MOTOR_DIR motor_left_dir, unsigned int motor_left_duty, MOTOR_DIR motor_right_dir, unsigned int motor_right_duty);

/*-----------------------------------------------
  TODO：左电机正转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_left_forward(unsigned char motor_duty);

/*-----------------------------------------------
  TODO：左电机反转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_left_back(unsigned char motor_duty);

/*-----------------------------------------------
  TODO：左电机停止
  参数：null
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_left_stop();

/*-----------------------------------------------
  TODO：右电机正转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_right_forward(unsigned char motor_duty);

/*-----------------------------------------------
  TODO：右电机反转
  参数：@motor_duty 电机PWM占空比
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_right_back(unsigned char motor_duty);

/*-----------------------------------------------
  TODO：右电机停止
  参数：null
  编写：夏杰
  日期：2017.4.13
------------------------------------------------*/
void motor_right_stop();


#endif