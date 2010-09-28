#!/usr/bin/python

# checksum and sign a Mifos release .zip file

template = """To verify the integrity of this file, use Gnu Privacy Guard: http://gnupg.org

Here is the command:

 gpg --verify %(filename)s.CHECKSUMS

To verify the checksums below, you can use OpenSSL: http://openssl.org

 openssl sha1 %(filename)s

Or

 openssl md5 %(filename)s

The hashes should match the ones below:
"""

import os
import sys
import shutil
import subprocess
import tempfile

def append(filename, text):
    out = open(tmp_filename, 'a')
    print >> out, text
    out.close()

def capture(filename, args):
    out = open(filename, 'a')
    subprocess.check_call([args[0], args[1]], stdout=out)
    out.close()

if len(sys.argv) < 2:
    print "Usage: %s RELEASE_ZIP" % sys.argv[0]
    sys.exit(1)

release_file = sys.argv[1]

if not os.path.isfile(release_file):
    print "ERROR: %s does not exist" % release_file
    sys.exit(1)

if os.path.getsize(release_file) < 1:
    print "ERROR: %s is less than one byte long" % release_file
    sys.exit(1)

final_filename = '%s.CHECKSUMS' % release_file

if os.path.isfile(final_filename):
    print "%s already exists and will be overwritten" % final_filename
    os.unlink(final_filename)
else:
    print "Creating checksum file for %s" % release_file

(tmp_fd, tmp_filename) = tempfile.mkstemp()
os.close(tmp_fd)

filename_without_path = os.path.basename(release_file)
append(tmp_filename, template % { 'filename': filename_without_path })
append(tmp_filename, "MD5:")
capture(tmp_filename, ['md5sum', release_file])
append(tmp_filename, "\nSHA-1:")
capture(tmp_filename, ['sha1sum', release_file])

subprocess.check_call(['gpg', '--clearsign', tmp_filename])

shutil.move('%s.asc' % tmp_filename, final_filename)
