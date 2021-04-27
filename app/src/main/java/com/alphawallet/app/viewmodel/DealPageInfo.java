package com.alphawallet.app.viewmodel;

import java.util.List;

public class DealPageInfo {

    /**
     * heco : {"routerContract":"0x....","headList":[{"name":"mdx","address":"0x25d2e80cb6b86881fd7e07dd263fb79f4abe033c"}],"commonList":[{"name":"can","address":"0x1e6395e6b059fc97a4dda925b6c5ebf19e05c69f"}]}
     * bsc : {"routerContract":"0x....","headList":[{"name":"wbnb","address":"0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c"}],"commonList":[{"name":"ada","address":"0x3ee2200efb3400fabb9aacf31297cbdd1d435d47"}]}
     */

    private HecoBean heco;
    private BscBean bsc;

    public HecoBean getHeco() {
        return heco;
    }

    public void setHeco(HecoBean heco) {
        this.heco = heco;
    }

    public BscBean getBsc() {
        return bsc;
    }

    public void setBsc(BscBean bsc) {
        this.bsc = bsc;
    }

    public static class HecoBean {
        /**
         * routerContract : 0x....
         * headList : [{"name":"mdx","address":"0x25d2e80cb6b86881fd7e07dd263fb79f4abe033c"}]
         * commonList : [{"name":"can","address":"0x1e6395e6b059fc97a4dda925b6c5ebf19e05c69f"}]
         */

        private String routerContract;
        private List<HeadListBean> headList;
        private List<CommonListBean> commonList;

        public String getRouterContract() {
            return routerContract;
        }

        public void setRouterContract(String routerContract) {
            this.routerContract = routerContract;
        }

        public List<HeadListBean> getHeadList() {
            return headList;
        }

        public void setHeadList(List<HeadListBean> headList) {
            this.headList = headList;
        }

        public List<CommonListBean> getCommonList() {
            return commonList;
        }

        public void setCommonList(List<CommonListBean> commonList) {
            this.commonList = commonList;
        }

        public static class HeadListBean {
            /**
             * name : mdx
             * address : 0x25d2e80cb6b86881fd7e07dd263fb79f4abe033c
             */

            private String name;
            private String address;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }

        public static class CommonListBean {
            /**
             * name : can
             * address : 0x1e6395e6b059fc97a4dda925b6c5ebf19e05c69f
             */

            private String name;
            private String address;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }
    }

    public static class BscBean {
        /**
         * routerContract : 0x....
         * headList : [{"name":"wbnb","address":"0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c"}]
         * commonList : [{"name":"ada","address":"0x3ee2200efb3400fabb9aacf31297cbdd1d435d47"}]
         */

        private String routerContract;
        private List<HeadListBeanX> headList;
        private List<CommonListBeanX> commonList;

        public String getRouterContract() {
            return routerContract;
        }

        public void setRouterContract(String routerContract) {
            this.routerContract = routerContract;
        }

        public List<HeadListBeanX> getHeadList() {
            return headList;
        }

        public void setHeadList(List<HeadListBeanX> headList) {
            this.headList = headList;
        }

        public List<CommonListBeanX> getCommonList() {
            return commonList;
        }

        public void setCommonList(List<CommonListBeanX> commonList) {
            this.commonList = commonList;
        }

        public static class HeadListBeanX {
            /**
             * name : wbnb
             * address : 0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c
             */

            private String name;
            private String address;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }

        public static class CommonListBeanX {
            /**
             * name : ada
             * address : 0x3ee2200efb3400fabb9aacf31297cbdd1d435d47
             */

            private String name;
            private String address;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }
        }
    }
}
