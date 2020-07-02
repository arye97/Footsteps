"""
A script that converts JSON strings into java code that can be used in our 
unit tests.  

When writing unit tests involving JSON data, we found that it was cleaner to 
write JSON key, values as a comma seperated list and use a Java method to 
convert that to a JSON string.
"""
import re
import json
from tkinter import *
from tkinter.scrolledtext import ScrolledText


JSON_METHOD_NAME = "JsonConverter.toJson"
MAP_METHOD_NAME = "JsonConverter.toMap"

def dict_to_code(json_dict):
    if isinstance(json_dict, str):
        return '"' + json_dict + '"'
    
    string = ""
    for key, value in json_dict.items():
        
        # Value is array
        if isinstance(value, list):
            string += '"{}", new Object[]'.format(key) + '{'
            string += ', '.join(dict_to_code(item) for item in value)
            string += '},\n'
        
        # Value is a nested json
        elif isinstance(value, dict):
            string += '"{}", {}({}),\n'.format(key, MAP_METHOD_NAME, dict_to_code(value).rstrip(",\n"))
        
        # Value is string, int, etc.
        elif isinstance(value, str):
            string += '"{}", "{}",\n'.format(key, value)
        
        # Value is int, float, etc.  No quotes.
        else:
            string += '"{}", {},\n'.format(key, value)
        
    return string
    

def convert():
    input_prefix = ""
    global text_box
    json_string = text_box.get("1.0",END)
    
    # Try to convert directly
    try:
        json_dict = json.loads(json_string)
    # Fallback
    except json.decoder.JSONDecodeError:
        input_prefix = json_string[0 : json_string.find('=') + 1]
        if not input_prefix[-1].isspace(): input_prefix += ' '
        json_string = json_string[json_string.find('=') + 1 :]  # Remove variable assignment
        json_string = re.sub(r'[^\S ]+', '', json_string)  # Remove all whitespace
        json_string = eval(json_string.strip().rstrip(';'))
        json_dict = json.loads(json_string)
    
    
    output_string = input_prefix + JSON_METHOD_NAME + "(true, \n"
    output_string += dict_to_code(json_dict).rstrip(",\n")
    output_string += ");"
    
    print(output_string)
    
    text_box.delete(1.0,"end")
    text_box.insert(1.0, output_string)
    



root = Tk()
root.configure(background='black')
text_box = ScrolledText(root)
text_box.grid(row=0)
Button(root, text="Convert", command=convert).grid(row=1, pady=8)
root.mainloop()