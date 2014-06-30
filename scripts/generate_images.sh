#!/bin/bash
#
# Creates images according to the Android standards.
#
# There is a 3:4:6:8 scaling ratio between the four primary densities. So,
# a 9x9 bitmap in ldpi is 12x12 in mdpi, 18x18 in hdpi and 24x24 in xhdpi.
#
# http://developer.android.com/design/style/iconography.html

BASE_PX_VALUE=12
SVG_IMAGES_DIR='images-svg'

for DPI in ldpi mdpi hdpi xhdpi; do
	# Calculates the base size for this DPI
	case "$DPI" in
		"ldpi")  PX_VALUE=$(($BASE_PX_VALUE * 3)) ;;
		"mdpi")  PX_VALUE=$(($BASE_PX_VALUE * 4)) ;;
		"hdpi")  PX_VALUE=$(($BASE_PX_VALUE * 6)) ;;
		"xhdpi") PX_VALUE=$(($BASE_PX_VALUE * 8)) ;;
		*) echo 'DPI invalido!' && exit 1 ;;
	esac

	# Creates the directory to store the images
	mkdir -p "drawable-$DPI"

	# Clouds
	for SVG in $SVG_IMAGES_DIR/cloud_l*.svg; do
		inkscape -D -h $(($PX_VALUE * 2)) -e "drawable-$DPI/${SVG/.svg/.png}" "$SVG"
	done
	for SVG in $SVG_IMAGES_DIR/cloud_m*.svg; do
		inkscape -D -h $PX_VALUE -e "drawable-$DPI/${SVG/.svg/.png}" "$SVG"
	done
	for SVG in $SVG_IMAGES_DIR/cloud_s*.svg; do
		inkscape -D -h $(($PX_VALUE / 2)) -e "drawable-$DPI/${SVG/.svg/.png}" "$SVG"
	done

	# Stars
	inkscape -D -w $PX_VALUE -e "drawable-$DPI/star_large.png" "$SVG_IMAGES_DIR/star_large.svg"
	inkscape -D -w $(($PX_VALUE / 3)) -e "drawable-$DPI/star_small.png" "$SVG_IMAGES_DIR/star_small.svg"
done
