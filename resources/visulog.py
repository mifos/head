#!/usr/bin/python -tt

'''
Colorize Mifos Tomcat log output based on log message severity level.
'''

colormap = {
    'INFO': 'green',
    'DEBUG': 'grey faint',
    'WARN': 'blue',
    'ERROR': 'red',
}

'''
ANSI Terminal Color and Attribute code borrowed from Activestate Python site,
recipe 574451.
'''

import sys

esc = '%s['%chr(27)
reset = '%s0m'%esc
format = '1;%dm'
fgoffset, bgoffset = 30, 40
for k, v in dict(
    attrs = 'none bold faint italic underline blink fast reverse concealed',
    colors = 'grey red green yellow blue magenta cyan white'
).items(): globals()[k]=dict((s,i) for i,s in enumerate(v.split()))

def term(arg=None, sep=' ', end='\n'):
    '''
        "arg" is a string or None
        if "arg" is None : the terminal is reset to his default values.
        if "arg" is a string it must contain "sep" separated values.
        if args are found in globals "attrs" or "colors", or start with "@" \
    they are interpreted as ANSI commands else they are output as text.
        colors, if any, must be first (foreground first then background)
        you can not specify a background color alone ; \
    if you specify only one color, it will be the foreground one.
        @* commands handle the screen and the cursor :
            @x;y : go to xy
            @    : go to 1;1
            @@   : clear screen and go to 1;1

        examples:
    term('red')                  : set red as the foreground color
    term('red blue')             : red on blue
    term('red blink')            : blinking red
    term()                       : restore terminal default values
    term('reverse')              : swap default colors
    term('cyan blue reverse')    : blue on cyan <=> term('blue cyan)
    term('red reverse')          : a way to set up the background only
    term('red reverse blink')    : you can specify any combinaison of \
            attributes in any order with or without colors
    term('blink Python')         : output a blinking 'Python'
    term('@@ hello')             : clear the screen and print 'hello' at 1;1
    '''
    cmd, txt = [reset], []
    if arg:
        arglist=arg.split(sep)
        for offset in (fgoffset, bgoffset):
            if arglist and arglist[0] in colors:
                cmd.append(format % (colors[arglist.pop(0)]+offset))
        for a in arglist:
            c=format % attrs[a] if a in attrs else None
            if c and c not in cmd:
                cmd.append(c)
            else:
                if a.startswith('@'):
                    a=a[1:]
                    if a=='@':
                        cmd.append('2J')
                        cmd.append('H')
                    else:
                        cmd.append('%sH'%a)
                else:
                    txt.append(a)
    if txt and end: txt[-1]+=end
    sys.stdout.write(esc.join(cmd)+sep.join(txt))

'''
Mifos log colorization filter code follows.
'''

import fileinput
import re
import sys

if __name__ == '__main__':
    try:
        while True: 
            try:
                line = sys.stdin.readline()
                if line == '': break
                m = re.match('([A-Z]+)', line)
                if m: term(colormap.get(m.group(0)))
                print line,
                term() # reset to default 
            except KeyboardInterrupt:
                # Ignore interrupt (ie: SIGINT/CTRL-C). Whatever provides our
                # stdin also receives the interrupt. If that other program
                # exits as a result of the interrupt, we'll get a SIGCHLD or
                # SIGPIPE or something so we'll also exit eventually and return
                # control to the console.
                continue
    except:
        raise
    finally:
        term() # reset to default 
