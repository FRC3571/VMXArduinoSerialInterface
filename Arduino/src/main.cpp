#define VMX_ONLY true
#define MAX_COMMAND_SIZE 50

#include <Arduino.h>

char command[MAX_COMMAND_SIZE] = {};
// using an index int is faster than going through the whole array every single time
int cmdIndex = 0;

/*
 * Parses a C-Style string for VMX commands and executes them accordingly.
 * Returns the error code of the operation, more information can be found in the documentation for the VMX / WPI code.
 */
int parseCommand(char* command) {
    char* token = strtok(command, " ");

    if (VMX_ONLY && strcmp(token, "VMX")) {
        return 2;
    }
  
    token = strtok(NULL, " ");

    if (!strcmp(token, "PINMODE")) {
        // NULL in strtok simply refers to the return value of the last successful strtok() operation
        // In this case that would be token
        int pin = atoi(strtok(NULL, " "));
        
        char* modeStr = strtok(NULL, " ");
        int mode = !strcmp(modeStr, "OUTPUT") ? OUTPUT : 
            !strcmp(modeStr, "INPUT") ? INPUT : INPUT_PULLUP;

        pinMode(pin, mode);
    } else if (!strcmp(token, "DIGITALWRITE")) {
        int pin = atoi(strtok(NULL, " "));
        // 0 if true, 1 if false
        int voltage = strcmp(strtok(NULL, " "), "LOW");

        digitalWrite(pin, voltage);

    } else if (!strcmp(token, "DIGITALREAD")) {
        int pin = atoi(strtok(NULL, " "));

        int voltage = digitalRead(pin);
        Serial.println(voltage);
        // a code of -1 means don't print an error code
        // I chose to do this becuase it's probably better for the function to only return error codes as opposed to error codes AND voltage codes
        return -1;
    } else if (!strcmp(token, "ANALOGWRITE")) {
        int pin = atoi(strtok(NULL, " "));
        int pwmWave = atoi(strtok(NULL, " "));

        analogWrite(pin, pwmWave);
    } else if (!strcmp(token, "ANALOGREAD")) {
        int pin = atoi(strtok(NULL, " "));
        int pwmWave = analogRead(pin);
        
        Serial.println(pwmWave);

        return -1;
    }
    
    else {
        return 5;
    }

    return 0;
}

void setup() {
    Serial.begin(9600);
    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, LOW);
}

void loop() {

    // Read bytes until a newline character is encountered, then send it to parseCommand()
    if(Serial.available()) {
        char chr = Serial.read();

        if(chr == '\n') {
            int errorCode = parseCommand(command);
            if (errorCode > -1) {
                Serial.print("code ");
                Serial.println(errorCode);
            }

            // Essentially sets the string to 0 because that's how the null terminator works
            command[0] = '\0';
            cmdIndex = 0;
        } else {
            command[cmdIndex] = chr;
            ++cmdIndex;
            // required to prevent segmentation faults, as overwriting the delimiter means there's no end and one needs to be made
            command[cmdIndex] = '\0';
        }
    }

}