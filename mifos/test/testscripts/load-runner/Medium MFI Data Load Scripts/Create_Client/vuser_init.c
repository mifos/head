#include "web_api.h"
#include "lrw_custom_body.h"

//  In this section, we can initialize all the variables needed for the all actions

int i=0;                      	//Variable to increment loops
int j;
char anyString[10]; 			//Used to Convert an integer to String
int office_Id;
int office_num=2;          // Used in Define_Office
int client_lastname=0;     // Used in Create_Client_and_SavingsAccount , intialises Clients last name, e.g CLIENT NO_6000
int loan_officer_Id;		// Used to increment loan officers	
int last_value;
int Length;
char *Found;

vuser_init()
{
	return 0;
}
