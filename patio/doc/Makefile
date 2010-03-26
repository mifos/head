# Makefile for Grails talk for Mifos Action Camp, March 2010.
# Copyright (C)2010 Adam Monsen <haircut@gmail.com>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

SHELL = /bin/bash

all: slides.pdf notes.pdf miniclean

slides.pdf: outline.otl
	otl2beamer.py $< > slides.tex
	pdflatex slides.tex

notes.pdf: outline.otl
	otl2beamer.py --notes $< > notes.tex
	pdflatex notes.tex

clean: miniclean
	$(RM) *.pdf

miniclean:
	$(RM) *.{aux,log,nav,out,snm,tex,toc,vrb}
	$(RM) grails.pdf grails-notes.pdf
