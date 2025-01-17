import input.Input;
import core.Engine;

public class Main {
    public static void main(String[] args) {
        Engine m_Engine = new Engine();
        Input m_Input = new Input();

        m_Engine.Initialize();
        m_Input.Initialize(m_Engine.GetCanvas());

        m_Engine.Start(m_Input);
    }
}
