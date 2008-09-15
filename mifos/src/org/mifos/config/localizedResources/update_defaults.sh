#!/bin/sh

# NAME
#     update_defaults
#
# SYNOPSIS
#     ./update_defaults.sh
# 
# DESCRIPTION
#     TBD
#
# OPTIONS
#     None.

po2prop -t messages.properties en/messages_en.po > messages.properties
