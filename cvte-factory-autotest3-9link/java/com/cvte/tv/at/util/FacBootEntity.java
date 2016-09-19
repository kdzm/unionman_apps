
package com.cvte.tv.at.util;

/**
 * @Description P: 1 = 1440*900, 2 = 1366*768, 3 = 1920*1080
 * @Description M: 0~12 LVDS MAP(no design)
 * @Description E: 0~1, 1 = Close LVDS SSD
 * @Description V: 0~1, 1 = Auto Adjust VGA(no design)
 * @Description Y: 0~1, 1 = Auto Adjust Ypbpr(no design)
 * @Description F: 0~1, 1 = Enter AT Mode(no need)
 * @Description R: 0~1, 1 = Reset System Next Power On(no design)
 * @Description B: 0~1, 1 = Enter AgingMode
 * @Description T: 0~1, 1 = Enter CVTE Lib AgingMode(no design)
 * @author Leajen_Ren
 * @date 2014-6-23
 * @version V1.0
 */
public class FacBootEntity {
    private int P_BIT = 0;
    private int M_BIT = 0;
    private int E_BIT = 0;
    private int V_BIT = 0;
    private int F_BIT = 0;
    private int R_BIT = 0;
    private int B_BIT = 0;
    private int T_BIT = 0;

    public FacBootEntity()
    {
        P_BIT = 0;
        M_BIT = 0;
        E_BIT = 0;
        V_BIT = 0;
        F_BIT = 0;
        R_BIT = 0;
        B_BIT = 0;
        T_BIT = 0;
    }

    /**
     * @return the p_BIT
     */
    public int getP_BIT() {
        return P_BIT;
    }

    /**
     * @param p_BIT the p_BIT to set
     */
    public void setP_BIT(int p_BIT) {
        P_BIT = p_BIT;
    }

    /**
     * @return the m_BIT
     */
    public int getM_BIT() {
        return M_BIT;
    }

    /**
     * @param m_BIT the m_BIT to set
     */
    public void setM_BIT(int m_BIT) {
        M_BIT = m_BIT;
    }

    /**
     * @return the e_BIT
     */
    public int getE_BIT() {
        return E_BIT;
    }

    /**
     * @param e_BIT the e_BIT to set
     */
    public void setE_BIT(int e_BIT) {
        E_BIT = e_BIT;
    }

    /**
     * @return the v_BIT
     */
    public int getV_BIT() {
        return V_BIT;
    }

    /**
     * @param v_BIT the v_BIT to set
     */
    public void setV_BIT(int v_BIT) {
        V_BIT = v_BIT;
    }

    /**
     * @return the f_BIT
     */
    public int getF_BIT() {
        return F_BIT;
    }

    /**
     * @param f_BIT the f_BIT to set
     */
    public void setF_BIT(int f_BIT) {
        F_BIT = f_BIT;
    }

    /**
     * @return the r_BIT
     */
    public int getR_BIT() {
        return R_BIT;
    }

    /**
     * @param r_BIT the r_BIT to set
     */
    public void setR_BIT(int r_BIT) {
        R_BIT = r_BIT;
    }

    /**
     * @return the b_BIT
     */
    public int getB_BIT() {
        return B_BIT;
    }

    /**
     * @param b_BIT the b_BIT to set
     */
    public void setB_BIT(int b_BIT) {
        B_BIT = b_BIT;
    }

    /**
     * @return the t_BIT
     */
    public int getT_BIT() {
        return T_BIT;
    }

    /**
     * @param t_BIT the t_BIT to set
     */
    public void setT_BIT(int t_BIT) {
        T_BIT = t_BIT;
    }

}
