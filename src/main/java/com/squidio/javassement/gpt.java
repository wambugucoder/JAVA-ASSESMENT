 public static boolean checkProductAvailable(HashMap<String, ProductPriceAndAvailabilityDTO> productPriceAndAvailabilityByAllProductCode,
                                                HashMap<String, ProductAvailabilityMap> productTypesAvailabilityMap, String productCode, String productType, String productQuantity,
                                                double length, double height, double width, double weight, boolean isCalledFromSwing, HashMap<String, HashMap<String, Integer>> productCodesAvailableInStandbyMap, List<ProductParameters> vehicleMeasurementUnits, HashMap<String, Integer> numberOfStandByProducts) {
        //START BCFER-6232
        boolean productsAvailable = true;
        boolean productsAvailableInStandByQty = false;
        boolean productsAvailableInStandByLen = false;
        boolean productsAvailableInStandByHei = false;
        boolean productsAvailableInStandByWid = false;
        boolean productsAvailableInStandByWei = false;
        HashMap<String, Integer> productMeasurementUnitStandbyAvailability = productCodesAvailableInStandbyMap.get(productCode);
        if (productMeasurementUnitStandbyAvailability == null) {
            productMeasurementUnitStandbyAvailability = new HashMap<>();
        }
        Double totalStandbyCapacityQtyAvailable = null;
        Double totalStandbyCapacityLenAvailable = null;
        Double totalStandbyCapacityWeiAvailable = null;
        Double totalStandbyCapacityWidAvailable = null;
        Double totalStandbyCapacityHeiAvailable = null;
        HashMap<String, Double> measurementUnitStandBySoldCapacity = new HashMap<>();//BCFER-7489
        if (productPriceAndAvailabilityByAllProductCode != null
                && productPriceAndAvailabilityByAllProductCode.get(productCode) != null
                && productPriceAndAvailabilityByAllProductCode.get(productCode).getMapMeasurementUnitStandByCapacity() != null) {
            HashMap<String, Double> mapMeasurementUnitStandByCapacity = productPriceAndAvailabilityByAllProductCode.get(productCode).getMapMeasurementUnitStandByCapacity();
            for (String unit : mapMeasurementUnitStandByCapacity.keySet()) {
                if (unit.equals("QUANTITY")) {
                    totalStandbyCapacityQtyAvailable = mapMeasurementUnitStandByCapacity.get(unit);
                } else if (unit.equals("LENGTH")) {
                    totalStandbyCapacityLenAvailable = mapMeasurementUnitStandByCapacity.get(unit);
                } else if (unit.equals("HEIGTH")) {
                    totalStandbyCapacityHeiAvailable = mapMeasurementUnitStandByCapacity.get(unit);
                } else if (unit.equals("WIDTH")) {
                    totalStandbyCapacityWidAvailable = mapMeasurementUnitStandByCapacity.get(unit);
                } else if (unit.equals("WEIGHT")) {
                    totalStandbyCapacityWeiAvailable = mapMeasurementUnitStandByCapacity.get(unit);
                }
            }
            //START BCFER-7489
            if (productPriceAndAvailabilityByAllProductCode.get(productCode).getMapMeasurementUnitStandBySoldCapacity() != null) {
                measurementUnitStandBySoldCapacity = productPriceAndAvailabilityByAllProductCode.get(productCode).getMapMeasurementUnitStandBySoldCapacity();
            }
//            if (productPriceAndAvailabilityByAllProductCode.get(productCode).getMapMeasurementUnitStandBySoldCapacity() != null) {
//                HashMap<String, Double> mapMeasurementUnitStandBySoldCapacity = productPriceAndAvailabilityByAllProductCode.get(productCode).getMapMeasurementUnitStandBySoldCapacity();
//                for (String unit : mapMeasurementUnitStandBySoldCapacity.keySet()) {
//                    if (mapMeasurementUnitStandBySoldCapacity.get(unit) != null) {
//                        if (unit.equals("QUANTITY") && totalStandbyCapacityQtyAvailable != null) {
//                            totalStandbyCapacityQtyAvailableLessSold = totalStandbyCapacityQtyAvailable - mapMeasurementUnitStandBySoldCapacity.get(unit);
//                        } else if (unit.equals("LENGTH") && totalStandbyCapacityLenAvailable != null) {
//                            totalStandbyCapacityLenAvailable = totalStandbyCapacityLenAvailable - mapMeasurementUnitStandBySoldCapacity.get(unit);
//                        } else if (unit.equals("HEIGTH") && totalStandbyCapacityHeiAvailable != null) {
//                            totalStandbyCapacityHeiAvailableLessSold = totalStandbyCapacityHeiAvailable - mapMeasurementUnitStandBySoldCapacity.get(unit);
//                        } else if (unit.equals("WIDTH") && totalStandbyCapacityWidAvailable != null) {
//                            totalStandbyCapacityWidAvailableLessSold = totalStandbyCapacityWidAvailable - mapMeasurementUnitStandBySoldCapacity.get(unit);
//                        } else if (unit.equals("WEIGHT") && totalStandbyCapacityWeiAvailable != null) {
//                            totalStandbyCapacityWeiAvailableLessSold = totalStandbyCapacityWeiAvailable - mapMeasurementUnitStandBySoldCapacity.get(unit);
//                        }
//                    }
//                }
//            }//END BCFER-7489
        }
        //END BCFER-6232
        // check availability
        int standByproducts = 0;
        ProductAvailabilityMap availabilityMap = productTypesAvailabilityMap.get(productType);
        if (availabilityMap != null && availabilityMap.containsKey("999|" + productCode)) { //SL-1678
            Collection<ProductInfoSearchSailingResultDTO> colls = availabilityMap.get("999|" + productCode);
            //    ArrayList<ProductInfoSearchSailingResultDTO> sortedColls = sortOrder(new ArrayList<>(colls));

            Map<String, ProductInfoSearchSailingResultDTO> measurementUnitToProductMap = new HashMap<>();
            for (ProductInfoSearchSailingResultDTO productInfoSearchSailingResultDTO : colls) {
                String productMeasurementUnit = productInfoSearchSailingResultDTO.getProductMeasurementUnit();
                if ("QTY".equals(productMeasurementUnit)) {
                    measurementUnitToProductMap.put(productMeasurementUnit, productInfoSearchSailingResultDTO);
                } else if (vehicleMeasurementUnits != null && productMeasurementUnit != null) {
                    switch (productMeasurementUnit) {
                        case "LEN":
                        case "WEI":
                        case "WID":
                        case "HEI":
                            measurementUnitToProductMap.put(productMeasurementUnit, productInfoSearchSailingResultDTO);
                            break;
                        // Add cases for other measurement units as needed
                    }
                }
            }

            OUTER:
            for (int x = 0; x < Integer.parseInt(productQuantity); x++) {
                ProductInfoSearchSailingResultDTO productInfoSearchSailingResult = null;
                String productMeasurementUnit = null;
                if (measurementUnitToProductMap.containsKey("QTY")) {
                    productInfoSearchSailingResult = measurementUnitToProductMap.get("QTY");
                    productMeasurementUnit = "QTY";
                } else if (measurementUnitToProductMap.containsKey("LEN")) {
                    productInfoSearchSailingResult = measurementUnitToProductMap.get("LEN");
                    productMeasurementUnit = "LEN";
                } else if (measurementUnitToProductMap.containsKey("WEI")) {
                    productInfoSearchSailingResult = measurementUnitToProductMap.get("WEI");
                    productMeasurementUnit = "WEI";
                } else if (measurementUnitToProductMap.containsKey("WID")) {
                    productInfoSearchSailingResult = measurementUnitToProductMap.get("WID");
                    productMeasurementUnit = "WID";
                } else if (measurementUnitToProductMap.containsKey("HEI")) {
                    productInfoSearchSailingResult = measurementUnitToProductMap.get("HEI");
                    productMeasurementUnit = "HEI";
                }
                // Add cases for other measurement units as needed
                if (productInfoSearchSailingResult == null) {
                    // No matching product found, handle error or break out of loop as appropriate
                    break;
                }
                if ("QTY".equals(productMeasurementUnit)) {
                    if (productInfoSearchSailingResult.getAvailability() < Double.parseDouble(productQuantity)) {
                        productsAvailable = false;
                        //START BCFER-6232
                        if (totalStandbyCapacityQtyAvailable != null && totalStandbyCapacityQtyAvailable > 0
                                && productInfoSearchSailingResult.getAvailability() + totalStandbyCapacityQtyAvailable >= Double.parseDouble(productQuantity)) {
                            productsAvailableInStandByQty = true;
                            if (!productCodesAvailableInStandbyMap.containsKey(productCode)) {
                                productCodesAvailableInStandbyMap.put(productCode, productMeasurementUnitStandbyAvailability);
                            }
                            productMeasurementUnitStandbyAvailability.put("QTY", totalStandbyCapacityQtyAvailable.intValue());
                        } else {
                            productsAvailableInStandByQty = false;
                            productMeasurementUnitStandbyAvailability.put("QTY", 0);
                        }
                        if (!isCalledFromSwing && !productsAvailable/*BCFER-8316*/) {
                            break;
                        }
                        //END BCFER-6232
                    }
                } else if (vehicleMeasurementUnits != null && productMeasurementUnit != null) {
                    switch (productMeasurementUnit) {
                        case "LEN":
                            if (productInfoSearchSailingResult.getAvailability() < length) {
                                productsAvailable = false;
                            }
                            //START BCFER-7489
                            if (!measurementUnitStandBySoldCapacity.isEmpty() && measurementUnitStandBySoldCapacity.get("LENGTH") >= productInfoSearchSailingResult.getAvailability()) {
                                productsAvailable = false;
                            }//END BCFER-7489
                            //START BCFER-6232
                            if (totalStandbyCapacityLenAvailable != null && totalStandbyCapacityLenAvailable > 0
                                    && totalStandbyCapacityLenAvailable >= length) {
                                productsAvailableInStandByLen = true;
                                if (!productCodesAvailableInStandbyMap.containsKey(productCode)) {
                                    productCodesAvailableInStandbyMap.put(productCode, productMeasurementUnitStandbyAvailability);
                                }
                                productMeasurementUnitStandbyAvailability.put("LEN", totalStandbyCapacityLenAvailable.intValue());
                            } else {
                                productsAvailableInStandByLen = false;
                                productMeasurementUnitStandbyAvailability.put("LEN", 0);
                            }
                            if (!isCalledFromSwing && !productsAvailable/*BCFER-8316*/) {
                                break OUTER;
                            }

                            if (productsAvailableInStandByLen) {
                                totalStandbyCapacityLenAvailable -= length;
                            }
                            //END BCFER-6232
                            break;
                        case "WID":
                            if (productInfoSearchSailingResult.getAvailability() < width) {
                                productsAvailable = false;
                            }
                            //START BCFER-7489
                            if (!measurementUnitStandBySoldCapacity.isEmpty() && measurementUnitStandBySoldCapacity.get("WIDTH") >= productInfoSearchSailingResult.getAvailability()) {
                                productsAvailable = false;
                            }//END BCFER-7489
                            //START BCFER-6232
                            if (totalStandbyCapacityWidAvailable != null && totalStandbyCapacityWidAvailable > 0
                                    && totalStandbyCapacityWidAvailable >= width) {
                                productsAvailableInStandByWid = true;
                                if (!productCodesAvailableInStandbyMap.containsKey(productCode)) {
                                    productCodesAvailableInStandbyMap.put(productCode, productMeasurementUnitStandbyAvailability);
                                }
                                productMeasurementUnitStandbyAvailability.put("WID", totalStandbyCapacityWidAvailable.intValue());
                            } else {
                                productsAvailableInStandByWid = false;
                                productMeasurementUnitStandbyAvailability.put("WID", 0);
                            }
                            if (!isCalledFromSwing && !productsAvailable/*BCFER-8316*/) {
                                break OUTER;
                            }
                            if (productsAvailableInStandByWid) {
                                totalStandbyCapacityWidAvailable -= width;
                            }
                            //END BCFER-6232
                            break;
                        case "WEI":
                            if (productInfoSearchSailingResult.getAvailability() < weight) {
                                productsAvailable = false;
                            }
                            //START BCFER-7489
                            if (!measurementUnitStandBySoldCapacity.isEmpty() && measurementUnitStandBySoldCapacity.get("WEIGHT") >= productInfoSearchSailingResult.getAvailability()) {
                                productsAvailable = false;
                            }//END BCFER-7489
                            //START BCFER-6232
                            if (totalStandbyCapacityWeiAvailable != null && totalStandbyCapacityWeiAvailable > 0
                                    && totalStandbyCapacityWeiAvailable >= weight) {
                                productsAvailableInStandByWei = true;
                                if (!productCodesAvailableInStandbyMap.containsKey(productCode)) {
                                    productCodesAvailableInStandbyMap.put(productCode, productMeasurementUnitStandbyAvailability);
                                }
                                productMeasurementUnitStandbyAvailability.put("WEI", totalStandbyCapacityWeiAvailable.intValue());
                            } else {
                                productsAvailableInStandByWei = false;
                                productMeasurementUnitStandbyAvailability.put("WEI", 0);
                            }
                            if (!isCalledFromSwing && !productsAvailable/*BCFER-8316*/) {
                                break OUTER;
                            }
                            //END BCFER-6232

                            if (productsAvailableInStandByWei) {
                                totalStandbyCapacityWeiAvailable -= weight;
                            }

                            break;
                        case "HEI":
                            if (productInfoSearchSailingResult.getAvailability() < height) {
                                productsAvailable = false;
                            }
                            //START BCFER-7489
                            if (!measurementUnitStandBySoldCapacity.isEmpty() && measurementUnitStandBySoldCapacity.get("HEIGHT") /*CFL-478*/ >= productInfoSearchSailingResult.getAvailability()) {
                                productsAvailable = false;
                            }//END BCFER-7489
                            //START BCFER-6232
                            if (totalStandbyCapacityHeiAvailable != null && totalStandbyCapacityHeiAvailable > 0
                                    && totalStandbyCapacityHeiAvailable >= height) {
                                productsAvailableInStandByHei = true;
                                if (!productCodesAvailableInStandbyMap.containsKey(productCode)) {
                                    productCodesAvailableInStandbyMap.put(productCode, productMeasurementUnitStandbyAvailability);
                                }
                                productMeasurementUnitStandbyAvailability.put("HEI", totalStandbyCapacityHeiAvailable.intValue());
                            } else {
                                productsAvailableInStandByHei = false;
                                productMeasurementUnitStandbyAvailability.put("HEI", 0);
                            }
                            if (!isCalledFromSwing && !productsAvailable/*BCFER-8316*/) {
                                break OUTER;
                            }
                            //END BCFER-6232
                            if (productsAvailableInStandByHei) {
                                totalStandbyCapacityHeiAvailable -= height;
                            }
                            break;
                        default:
                            break;
                    }
                }


                if (productsAvailable) {
                    double productAvaliable = productInfoSearchSailingResult.getAvailability();
                    productAvaliable -= length;
                    productInfoSearchSailingResult.setAvailability(productAvaliable);
                }

                if ((productsAvailableInStandByLen || productsAvailableInStandByHei || productsAvailableInStandByWei || productsAvailableInStandByWid) && (!productsAvailable)) {
                    standByproducts++;
                }

            }

            numberOfStandByProducts.put(productCode, standByproducts);

            //START BCFER-7373
            if (productsAvailable
                    || (productCodesAvailableInStandbyMap.containsKey(productCode)
                    && ((!productsAvailableInStandByHei && productMeasurementUnitStandbyAvailability.get("HEI") != null)
                    || (!productsAvailableInStandByLen && productMeasurementUnitStandbyAvailability.get("LEN") != null)
                    || (!productsAvailableInStandByQty && productMeasurementUnitStandbyAvailability.get("QTY") != null)
                    || (!productsAvailableInStandByWei && productMeasurementUnitStandbyAvailability.get("WEI") != null)
                    || (!productsAvailableInStandByWid && productMeasurementUnitStandbyAvailability.get("WID") != null)))) {
                productCodesAvailableInStandbyMap.remove(productCode);
            }
            //END BCFER-7373
        } else {
            productsAvailable = false;
        }
        return productsAvailable;
    }
