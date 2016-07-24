#!/usr/bin/perl

# Split dataset by name of article.

# usage: cat file.xml | split_by_name.pl

# Creates output directory "out"

use strict;

my $outdir = "out";
print `rm -rf $outdir`;
print `mkdir $outdir`;

my $fname = "";
my $buf = "";

while (<STDIN>) {
    chomp;
    if (/^\s*$/) {
	next;
    }
    if (/url=/) {
 	$buf = "$_\n";
	$fname = "$outdir/" . &getFileName($_); 
    }
    else {
	open OUT, ">>$fname" or die "can't write to $fname\n";	
	$buf .= $_;
	print OUT "$buf\n\n";
	close OUT;
    }
}

sub getFileName {
    my ($url) = @_;
    return ($url =~ /\/([^\/]+)$/) ? $1 : "UNK";
}
