#!/usr/bin/perl -w

# Counts number of relations per file in current directory.

use strict;

opendir MYDIR, ".";
my @contents = grep !/^\.\.?$/, readdir MYDIR;
closedir MYDIR;

foreach my $f (@contents) {
	open IN, $f or die "can't read $f\n";
	my $total = 0;
	while (<IN>) {
		while (/relation=/g) {
			$total++;
		}
	}
	print "$total\t$f\n";
	close IN;
}
