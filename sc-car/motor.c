/*-----------------------------------------------
  ���ƣ��������������
  ��д���Ľ�
  ���ڣ�2017.4.13
  ���ݣ�
------------------------------------------------*/

#include "sc.h"

// �������PWMռ�ձ�
uint motor_left_duty = 0, motor_right_duty = 0;

// ���PWM��������
uint motor_pwm_counter = 0, motor_pwm_t = 100;

/*-----------------------------------------------
  TODO�����������ʼ��
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_init()
{
	// ��ʼ�����ֹͣ
	motor_left_stop();
	motor_right_stop();
}

/*-----------------------------------------------
  TODO�������������ת��ռ�ձȿ���
  ������
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_control(MOTOR_DIR motor_left_dir, uint motor_left_duty, MOTOR_DIR motor_right_dir, uint motor_right_duty)
{
	// ���PWM������
	if(motor_pwm_counter >= motor_pwm_t)
	{
		motor_pwm_counter = 0;
	}
	
	// ��������
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

	// �ҵ������
	switch(motor_right_dir)
	{
		// �ҵ����ת
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
  TODO��������ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_left_forward(uchar motor_duty)
{
	// ��ƽ����������ת
	motor_left_in = MOTOR_ON;
	motor_left_out = MOTOR_OFF;
	
	// PWM������
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
  TODO��������ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_left_back(uchar motor_duty)
{
	motor_left_in = MOTOR_OFF;
	motor_left_out = MOTOR_ON;

	// PWM������
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
  TODO������ֹͣ
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_left_stop()
{
	// ��ƽ����
	motor_left_in = MOTOR_OFF;
	motor_left_out = MOTOR_OFF;

	// PWM��ռ�ձ�����
	motor_left_duty = 0;
	motor_right_duty = 0;
}

/*-----------------------------------------------
  TODO���ҵ����ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_right_forward(uchar motor_duty)
{	
	motor_right_in = MOTOR_ON;
	motor_right_out = MOTOR_OFF;

	// PWM������
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
  TODO���ҵ����ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_right_back(uchar motor_duty)
{
	motor_right_in = MOTOR_OFF;
	motor_right_out = MOTOR_ON;

	// PWM������
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
  TODO���ҵ��ֹͣ
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_right_stop()
{
	motor_right_in = MOTOR_OFF;
	motor_right_out = MOTOR_OFF;

	// PWM��ռ�ձ�����
	motor_left_duty = 0;
	motor_right_duty = 0;
}





