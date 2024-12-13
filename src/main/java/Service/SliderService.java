package Service;

import DAO.SliderDAO;
import Model.Slider;

import java.util.List;

public class SliderService {
    private SliderDAO sliderDAO =  new SliderDAO();
    public List<Slider> findAll() {
        return sliderDAO.findAll();
    }

}

