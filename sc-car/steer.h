#ifndef _STEER_H_
#define _STEER_H_

// 舵机信号输出端
sbit steer_out = P1^0;

// 舵机PWM占空比,范围:5/200~25/200 
extern unsigned char steer_duty;


#endif




