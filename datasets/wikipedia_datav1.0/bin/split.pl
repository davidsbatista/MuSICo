#!/usr/bin/perl -w

# split data into training/testing

# usage: split.pl <original_file> <training_proportion> <training_output> <testing_output>"

use strict;
use List::Util 'shuffle';


@ARGV==4 or die "usage: split.pl <original_file> <training_proportion> <training_output> <testing_output>\n";
my $f = $ARGV[0]; 
my @data = ();
open IN, $f or die "can't open $f\n";
my $buf = "";
while (<IN>) {
    if (/^\s*$/) {
	next;
    }
    if (/url=/) {
	if (!($buf eq "")) {
	    push @data, $buf . "\n";
	}
	$buf = $_;
    }
    else {
	$buf .= $_;
    }
}
srand(10);
@data = shuffle(@data);
my $endi = $ARGV[1] * @data;
my @training = @data[0..$endi];
my @testing = @data[$endi..@data-1];
print "training size: " . @training . " testing size: " .@testing . "\n";   
open OUT, ">$ARGV[2]" or die "can't write $ARGV[2]\n";
print OUT @training;
close OUT;
open OUT, ">$ARGV[3]" or die "can't write $ARGV[3]\n";
print OUT @testing;
close OUT;


