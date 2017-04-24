#ifndef _TIMER_H_
#define _TIMER_H_

// 定时器
#define TIMER_FULL_T 65536

// 定时器0定时周期:100us 
// (12M:timer_time = 100; 11.0582M:timer_time = 92;)
#define TIMER0_T 90

// 定时器1定时周期:200us 
// (12M:timer_time = 200; 11.0582M:timer_time = 184;)
#define TIMER1_T 180

// 变量定义
// 100us, 1ms, 1m 定时计数器
extern unsigned char timer_100us_counter, timer_ms_counter, timer_s_counter;


/*-----------------------------------------------
  TODO：定时器初始化
  参数：
  编写：夏杰
  日期：2017.4.10
------------------------------------------------*/
void timer_init();








#endif





