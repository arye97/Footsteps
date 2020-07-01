"""
A script that converts JSON strings into java code that can be used in our 
unit tests.  

When writing unit tests involving JSON data, we found that it was cleaner to 
write JSON key, values as a comma seperated list and use a Java method to 
convert that to a JSON string.
"""
import re

JSON_METHOD_NAME = "JsonConverter.toJason"

def convert(json_string):
    json_string = json_string.replace("\n", "")
    json_string = json_string.replace('\"', '"')
    print(json_string)