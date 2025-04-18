# 선형회귀분석 및 베이지안 선형회귀

## 선형회귀분석
### 모델에 대한 설명과 예시
선형회귀분석은 변수들 간의 함수관계를 분석하는 방법으로, 독립변수가 종속변수에 미치는 영향력의 크기를
파악하고, 이를 통해 독립변수의 일정한 값에 대응하는 종속변수 값을 예측하는 모형을 산출하는 방법입니다.
어떤 요인의 수치에 따라서 특정 요인의 수치가 영향을 받고 있는 상황으로, 어떤 변수의 값에 따라서 특정
변수의 값이 영향을 받고 있다고 볼 수 있습니다.

*Y=β0+β1X1+β2X2+⋯+βnXn+ϵ*
Y는 종속변수, X1,X2,⋯Xn는 독립변수, β0,β1,⋯βn는 회귀 계수, ϵ는 오차항입니다.
한 개의 독립변수가 있으면 단순 선형 회귀, 둘 이상이면 다중 선형 회귀인데, 저희는 '시간대'와 '요일'이라는
둘 이상의 독립변수를 가지고 있으므로 다중 선형 회귀입니다. 
β와 ϵ를 찾기 위해서 실제값과 가설로부터 얻은 예측값의 오차를 계산하는 식을 세우고, 이 식의 값을 최소화하는
최적의 β와 ϵ를 찾아냅니다. 이 때 실제값과 예측값에 대한 오차에 대한 식을 목적 함수 또는 비용함수 또는 손실함수라고 합니다.
함수의 값을 최소화하거나 최대화하거나 하는 목적을 가진 함수를 목적함수라 하고 이 값을 최소화하려고 하면 이를
비용함수 또는 손실함수라고 합니다.
Y는 실제 사람 수, X1,X2,⋯,Xk는 독립 변수(예: 감지된 기기 수, 시간대 더미 변수, 요일 더미 변수,)이다.

#### 평균 제곱 오차
회귀 문제의 경우에는 주로 평균 제곱 오차(Mean Squared Error, MSE)가 사용됩니다.
수식적으로 단순히 ‘오차 = 실제값 — 예측값’ 이라고 정의한 후에 모든 오차를 더하면 음수 오차도 있고, 양수 오차도 있으므로
오차의 절대적인 크기를 구할 수가 없습니다. 그래서 모든 오차를 제곱하여 더하는 방법을 사용합니다.
이때 데이터의 개수인 n으로 나누면, 오차의 제곱합에 대한 평균을 구할 수 있는데 이를 평균 제곱 오차(Mean Squered Error, MSE)라고 합니다.​
MSE= 1/n∑(i=1~n)(yi​−ti)^2 

### 본 프로젝트에 사용할 시의 적합성에 대한 평가 및 근거
시간과 요일을 독립 변수로 사용하는 경우, 이 변수들은 주로 범주형 데이터로 간주되는데, 선형 회귀 모델에서 
범주형 데이터를 처리하기 위해서는 이 변수들을 수치적으로 표현할 수 있는 더미 변수로 변환해야 합니다. 
예를 들어, 요일은 월요일부터 일요일까지 7개의 범주를 가지고 있으므로, 이를 6개의 더미 변수로 변환할 수 있습니다.
이러한 접근 방식은 모델을 단순화하고 해석을 용이하게 하며, 예측 성능을 유지할 수 있도록 도와줍니다.

#### 선형 회귀 모델의 적용 및 범주형 데이터 처리
1. 요일 더미 변수: 요일은 월요일부터 일요일까지 7개의 범주로 구분됩니다. 이 중 하나를 기준 범주로 선택하고
(일반적으로 일요일 또는 월요일), 나머지 6개의 요일에 대해 각각 더미 변수를 생성합니다.
예를 들어, 월요일이면 (1,0,0,0,0,0), 화요일이면 (0,1,0,0,0,0)과 같이 표현합니다.
2. 시간대 더미 변수: 시간대를 '아침', '오후', '저녁' 등으로 구분하여 각 시간대를 더미 변수로 표혷합니다.
예를 들어, 아침이면 (1,0), 오후면 (0,1),저녁이면 기준 범주로 하여 (0,0)으로 표현할 수 있습니다.

#### 모델 단순화 및 데이터 해석 용이
더미 변수를 사용함으로써 모델은 각 범주의 특정한 영향을 개별적으로 측정할 수 있게 됩니다. 
이는 모델의 해석을 용이하게 하며, 각 변수가 실제 사람 수에 미치는 영향을 명확하게 분리하여 보여줍니다. 
또한, 모델의 복잡도를 관리하고 과적합을 방지할 수 있도록 도와줍니다.

### 예제
구체적인 예제
예를 들어, 데이터 세트가 주중과 주말, 아침과 저녁 시간대에만 집중되어 있다고 가정해 봅시다. 
이 경우, 요일을 '주중'과 '주말'로 묶고, 시간대를 '아침'과 '저녁'으로 묶어 더미 변수를 생성할 수 있습니다. 
이러한 방식은 데이터의 특성을 반영하여 모델의 정확도를 높이는 동시에 변수의 개수를 줄여 계산 효율성을 증가시킵니다.
이러한 접근 방식은 모델을 단순화하고 데이터의 이해를 돕기 때문에 프로젝트에 적합하며, 실제 사용 조건에서의 성능을 개선할 수 있는 효과적인 방법입니다.
##### java 함수 구현 


'''java

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class DetailedLinearRegression {
    public static void main(String[] args) {
        // 데이터 초기화
        double[][] xData = new double[][] {
            // 예: 월요일 0시
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            // 예: 화요일 1시
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            // 예: 수요일 2시
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            // 추가 데이터...
        };
        double[] yData = {15, 18, 20}; // 실제 사람 수

        // 선형 회귀 모델 생성 및 데이터 추가
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(yData, xData);

        // 회귀 계수 추출
        double[] beta = regression.estimateRegressionParameters();

        // 결과 출력
        for (int i = 0; i < beta.length; i++) {
            System.out.println("Coefficient " + i + ": " + beta[i]);
        }

        // MSE 계산
        double mse = calculateMSE(regression, xData, yData);
        System.out.println("MSE: " + mse);
    }

    // MSE 계산 메소드
    private static double calculateMSE(OLSMultipleLinearRegression regression, double[][] xData, double[] yData) {
        double mse = 0;
        double[] predictions = regression.estimateResiduals(); // 이 메소드는 실제 값 - 예측 값의 배열을 반환
        for (double res : predictions) {
            mse += res * res;
        }
        mse /= xData.length;
        return mse;
    }
}'''

## 베이지안 선형 회귀 분석
### 모델에 대한 설명과 예시
베이지안 선형회귀는 전통적인 선형회귀 모델을 베이지안 통계적 접근 방식으로 확장한 것입니다.
베이지안 선형회귀에서는 계수 𝛽에 대해 사전 분포를 설정합니다. 보통 이 분포는 정규 분포로 가정하는 것이 일반적입니다. 
관측 데이터와 결합하여 베이즈 정리를 적용하면, 사후 분포를 얻을 수 있습니다. 이 분포는 주어진 데이터에 대한 모든 불확실성을 반영하고, 
추가 데이터가 주어질 때마다 업데이트됩니다.이 모델은 기존의 선형 회귀가 가정하는 고정된 계수 대신에, 계수를 확률 변수로 취급하고 이에 대한 사전 분포를 설정하여 데이터를 통해 사후 분포를 업데이트합니다.
베이지안 회귀는 가능도 최대화가 목적이다. 

확률이 모델에서 관측값이 나올 확률이라면, 가능도는 관측값에서 모델이 지닌 가능도를 평가 한다.선형 모델에서 오차 ϵ은 평균 0, 표준편차 σ의 정규분포를 가정한다. 이를 식으로 표현하면 다음과 같다
ϵ∼N(0,σ^2)
그리고 이를 통해 𝑦의 확률 분포를 나타내면 다음과 같다.
y∼N(θ1x+θ2,σ^2)
가능도는 정규분포의 확률밀도 함수를 통해 구할 수 있다. 
평균μ, 표준편차 𝜎인 정규분포를 생각해보자. x∼N(μ,σ^2) 이 정규분포의 확률밀도는 다음과 같다.
![image](https://github.com/jsub1379/AdventureDesign24-1/assets/164135983/3770c242-8fb8-4cb0-8085-7b2fe6829913)
이 때, 관측된 값 𝑥𝑖의 가능도는𝑥𝑖가 확률밀도에서 갖는 값을 나타낸다. 그리고 가능도는 모든 관측된 값의 가능도의 곱으로 나타낸다. 
우리의 관측값은 모델로부터 독립적으로 추출된 값이므로, 각 관측값에 대한 확률밀도의 곱이 가능도가 된다.
이를 이용해, 위의 𝑦식의 가능도, 즉, 관측값 𝑥,𝑦에서 현재 모델의 모수의 가능도를 구하면 다음과 같다.
![image](https://github.com/jsub1379/AdventureDesign24-1/assets/164135983/d665b05f-f784-4312-b120-c6fe15ee71a6)

베이즈 추론은 관측값을 통해, 모델의 확률분포를 업데이트 하는 추론이다.
관측값 𝑥,𝑦를 줄여서𝑥, 파라미터θ1,𝜃2를 줄여서 𝜃로 두자. 그럼 베이즈 식은 다음과 같다.
![image](https://github.com/jsub1379/AdventureDesign24-1/assets/164135983/6bba695d-efbb-4438-a6ee-cd0dd65264a8)
목적은 𝑥를 통해 θ의 확률분포를 구하는 것이다. 여기서P(θ)를 사전분포,𝑃(𝜃|𝑥)를 사후분포라고 한다. 이를 이용하면, 적당한 θ 범위에 대해서 관측값에 대한 사후분포를 구할 수 있다. 
하지만, θ의 개수가 늘어난다면, 우리가 사후분포를 탐색해야 하는 공간은 지수 함수 꼴로 늘어나기 때문에 파라미터 공간을 탐색을 하기 위한 전략을 짜야 한다.

여기서 등장하는 것이 마르코프 체인 몬테카를로법(MCMC)다. 마르코프 체인은 어떤 상태가 바로 전 상태에 의존하는 경우를 말한다. 
몬테카를로는 무작위 샘플링을 하는 것이니 둘을 합친 MCMC는 어떤 상태가 끊임없이 움직이고 있는 체인 상태의 샘플링을 말한다.
MCMC는 특정 조건에서 마르코프 체인이 정상 상태 분포(steady-state distribution)로 수속한다는 특징(ergodic)을 이용한다. 
마르코프 연쇄가 ergodic하기 위해서는, 전이 상태가 하나의 값으로 정착 되지 않을 조건, 그리고 전이가 주기성을 띄지 않을 조건을 충족해야 한다. 이 조건을 만족하는 전이 알고리즘을 이용하면 
어떤 파라미터 값에서 탐색을 시작하더라도 탐색 과정에서 발견된 값들이 이루는 분포가 정상 상태 분포를 이룰 것이고, 이 분포를 통해 모델 파라미터의 확률분포를 추정할 수 있다는 것이 MCMC 베이지안 추론이다. 

MCMC의 정상 상태 분포는 파라미터의 확률분포를 타겟으로 한다. 이를 위해서는 파라미터 값을 탐색할 때 마르코프 체인의 상태 전이 확률이 더 그럴싸한 파라미터 값의 상태로 전이를 해줘야 한다. 
즉, 샘플링이 더 그럴싸한 파라미터를 중심으로 이루어져야, 샘플링이 이루는 분포가 우리가 타겟으로 하는 파라미터 주변으로 이루어질 것이다.
이를 위해 Metropolis-Hastings(MH) 알고리즘이 고안 되었다. MH 알고리즘에서는 사전분포를 제안하고, 현재 샘플링 파라미터 θt가 다음 샘플 θnew로 전이될 확률을 사후확률의 비로 결정한다. 
우선 사후확률의 비를 식으로 표현하면 다음과 같다.
![image](https://github.com/jsub1379/AdventureDesign24-1/assets/164135983/59e9b078-04f4-4a41-b28a-b03085f7c14c)

그리고 전이 여부를 다음 식으로 결정한다. 
α(θt,θnew)=min(1,r)
윗 식들을 보면 𝑝(𝑥)가 사라지고, 가능도p(x|θ)와 사전확률분포 𝑝(𝜃)만으로 파라미터 탐색을 하고 있다는 것을 알 수 있다. 
사전 확률 분포는 MH알고리즘에서는 proposal 분포라고도 하는데, 사전에 파라미터에 대한 정보가 없는 경우, 이 분포는 정보를 포함하지 않는 uniform 분포나 normal 분포를 이용한다. 
위 파트에서 가능도를 구했고, 분포에 대한 정보는 설계자가 지정하므로, 우리는 r을 구할 수 있다.

그럼, 전이 확률을 살펴보자. 만약,𝜃𝑛𝑒𝑤에서의 사후확률이 θt보다 크다면 r은 1보다 크게 되어 다음 상태 θ𝑡+1은 θnew로 전이된다. 
만약 새로 샘플링 된 파라미터가 기존 파라미터보다 작은 사후확률을 갖는다면 r이 작아지며, 𝜃𝑛𝑒𝑤로 전이될 확률이 낮아지고, θt는 그대로 유지되어 θt+1이 되는 경우가 발생한다.
이런 식으로 파라미터 공간을 탐색하면 우리가 찾고자 하는 파라미터 부근 공간에서 효율적인 탐색을 할 수 있게 된다.
베이즈 추론에서 MH 알고리즘이 작동하는 원리를 간략하자면,  proposal 분포에서 추출한 파라미터를 그럴싸한 공간을 위주로 탐색하고, 그 탐색 경로가 이루는 분포를 통해 파라미터의 확률분포를 추정하는 알고리즘이다.

### 본 프로젝트에 사용할 시의 적합성에 대한 평가 및 근거
#### 데이터 불확실성 처리
베이지안 선형회귀는 계수의 불확실성을 확률 분포로 모델링하여 처리합니다. 
이는 프로젝트 데이터에 내재된 불확실성과 노이즈를 자연스럽게 통합할 수 있게 해줍니다. 
예를 들어, 모든 사람이 항상 동일한 기기 수를 가지고 다니지 않으며, 시간과 요일에 따라 기기 사용 패턴이 다르게 나타날 수 있습니다.

#### 사전 지식의 통합
베이지안 접근법은 사전 지식이나 이전 연구 결과를 모델에 통합할 수 있도록 해줍니다. 
이는 특히 새로운 데이터가 제한적일 때 유용하며, 예측 모델의 시작점을 제공할 수 있습니다. 
예를 들어, 이전에 수행된 유사 시간대와 요일의 데이터를 기반으로 초기 사전 분포를 설정할 수 있습니다.

#### 업데이트 가능한 모델링
데이터가 축적되면서 베이지안 모델은 추가된 데이터를 기반으로 사후 분포를 지속적으로 업데이트할 수 있습니다. 
이는 프로젝트가 진행됨에 따라 모델의 정확도와 신뢰도를 향상시킬 수 있게 해줍니다.

#### 복잡한 관계 모델링
시간과 요일 같은 범주형 변수는 더미 변수를 통해 모델에 통합될 수 있으며, 베이지안 선형회귀는 이러한 변수들 사이의 복잡한 상호작용을 포착할 수 있습니다.
이는 특정 시간대나 요일에 사람 수에 영향을 미치는 요인들을 보다 정밀하게 분석할 수 있게 해줍니다.

###예제
'''java
import org.apache.commons.math3.linear.*;

public class BayesianLinearRegressionSimulation {
    public static void main(String[] args) {
        // 데이터 초기화
        double[][] xData = {
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 월요일 0시
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 화요일 1시
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // 수요일 2시
        };
        double[] yData = {15, 18, 20}; // 실제 사람 수

        // 가중치와 학습률 초기화
        double[] beta = new double[xData[0].length]; // 회귀 계수
        double learningRate = 0.01;

        // 베이지안 회귀를 위한 반복 업데이트
        int iterations = 10000;
        for (int i = 0; i < iterations; i++) {
            // 데이터 포인트를 반복
            for (int j = 0; j < yData.length; j++) {
                double[] x = xData[j];
                double y = yData[j];
                double predicted = 0;

                // 예측값 계산
                for (int k = 0; k < beta.length; k++) {
                    predicted += beta[k] * x[k];
                }

                // 오차 및 업데이트
                double error = y - predicted;
                for (int k = 0; k < beta.length; k++) {
                    beta[k] += learningRate * error * x[k]; // 단순 선형회귀의 업데이트 식 사용
                }
            }
        }

        // 결과 출력
        System.out.print("Estimated coefficients: ");
        for (double b : beta) {
            System.out.print(b + " ");
        }
        System.out.println();
    }
}'''

