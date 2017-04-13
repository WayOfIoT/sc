#ifndef _MOTOR_H_
#define _MOTOR_H_

// �������˿�
sbit motor_left_in = P1^2;
sbit motor_left_out = P1^3;
sbit motor_right_in = P1^4;
sbit motor_right_out = P1^5;

// ���������ƽ
#define MOTOR_ON 1
#define MOTOR_OFF 0

// �������PWMռ�ձ�
extern unsigned int motor_left_duty, motor_right_duty;

// ���PWM��������
extern unsigned int motor_pwm_counter;

// �������ת��������
typedef enum MOTOR_DIR
{
	// ������ת
	motor_left_dir_forward,
	// ������ת
	motor_left_dir_back,
	// ����ֹͣ
	motor_left_dir_stop,
	// �ҵ����ת
	motor_right_dir_forward,
	// �ҵ����ת
	motor_right_dir_back,
	//  �ҵ��ֹͣ
	motor_right_dir_stop	
}MOTOR_DIR;

/*----------- �������� ------------*/

/*-----------------------------------------------
  TODO�����������ʼ��
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_init();

/*-----------------------------------------------
  TODO�������������ת��ռ�ձȿ���
  ������
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_control(MOTOR_DIR motor_left_dir, unsigned int motor_left_duty, MOTOR_DIR motor_right_dir, unsigned int motor_right_duty);

/*-----------------------------------------------
  TODO��������ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_left_forward(unsigned char motor_duty);

/*-----------------------------------------------
  TODO��������ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_left_back(unsigned char motor_duty);

/*-----------------------------------------------
  TODO������ֹͣ
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_left_stop();

/*-----------------------------------------------
  TODO���ҵ����ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_right_forward(unsigned char motor_duty);

/*-----------------------------------------------
  TODO���ҵ����ת
  ������@motor_duty ���PWMռ�ձ�
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_right_back(unsigned char motor_duty);

/*-----------------------------------------------
  TODO���ҵ��ֹͣ
  ������null
  ��д���Ľ�
  ���ڣ�2017.4.13
------------------------------------------------*/
void motor_right_stop();


#endif