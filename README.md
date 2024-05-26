## 포아송

### 모델에 대한 설명과 예시

이번 프로젝트에서는 포아송 회귀 모델을 사용하여 종속 변수(실제 사람 수)를 
알 수 있는 데이터 분석 모델을 만들 계획입니다. 

독립변수인 감지된 기기 개수는 종속 변수인 실제 사람 수와 비례하지만
오차가 발생할 수 있습니다. 

이런 오차를 줄이기 위해 저희는 시간대와 요일이라는 독립 변수를 추가하여 
총 3가지의 독립 변수로 종속 변수를 예측하는 데이터 분석 모델을 설계했습니다. 

포아송 분포의 확률 질량 함수는 다음과 같이 정의됩니다.
![image](https://github.com/jsub1379/AdventureDesign24-1/assets/164328587/03afd861-625c-4eea-a57e-3330dfeaab25)
* Y는 사건의 발생 횟수
* λ는 평균 발생 횟수

포아송 분포를 따르는 독립 변수들의 선형 조합의 지수 함수

![image](https://github.com/jsub1379/AdventureDesign24-1/assets/164328587/e273e807-b99d-466f-a6f8-d0b64ebcbab1)

위 식을 로그 변환하면 다음과 같은 포아송 로그 선형 모델이 됩니다.

![image](https://github.com/jsub1379/AdventureDesign24-1/assets/164328587/9533f5d2-fa84-48f3-86f9-b11c541ef25c)

* β0는 절편(intercept)으로서, 모든 독립 변수가 0일 때의 종속 변수의 예상값
* m,t,w는 각각 감지된 기기수, 시간, 요일
* log(λ)로 반환된 값은 실제 사람수(r)
* β1,β2,β3는 각각 𝑚,𝑡,𝑤의 회귀 계수


감지된 기기수(m)| 시간(t)| 요일(w)| 실제 사람수(r)
---|---|---|---|
13|4|5|10
7|16|6|5
3|4|2|?

각각의 데이터들을 수집해 초기에는 실제 사람수를 직접 관찰하여 데이터모델을 만든 후,
이후에는 데이터 모델을 통해 종속 변수인 실제 사람수를 예측할 수 있습니다. 


### 본 프로젝트에 사용할 시의 적합성에 대한 평가 및 근거

본 프로젝트에서 포아송 로그 선형 모델은 다음과 같은 이유에서 적합하다고 결론 내렸습니다. 

#### 포아송 분포
포아송 분포는 확률론에서 단위 시간 안에 어떤 사건이 몇 번 발생할 것인지를 표현하는 
이산 확률 분포입니다. 기기값을 토대로 실제 사람수를 측정해야하므로 횟수나 개수를 반환하는
이산형 통계 자료, 즉 포아송 분포가 적합하다고 생각하였습니다.

#### 관계성
독립변수와 종속변수간의 관계성을 잘 나타내어줄 수 있는 선형 모델이기 때문입니다. 
감지된 기기 값은 실제 사람수와 정비례 관계를 가지며
요일과 시간은 특정 요일 특정 시간에 따라 인구 밀도가 달라지기 때문에 
종속 변수인 실제 사람수를 예측하는데 효과적이라고 생각했습니다. 
예를 들어, 새벽 3시 혹은 주말에 경우에는 감지된 기기가 다른 시간대 다른 요일에 비해 
크게 감소될 것이고 이것은 종속 변수에 영향을 미칩니다. 

#### 독립변수의 다양성
단순 선형 회귀 분석과 같은 회귀 분석과는 달리 여러 독립 변수를 사용할 수 있습니다. 
저희는 독립 변수를 감지된 기기 하나만 사용한다면 기술적 한계에 의해 오차가 생겨 
정확한 종속 변수 값을 도출할 수 없을 것으로 생각했습니다. 
그래서 오차를 줄일 수 있도록 다른 독립 변수를 더 추가하고자 했고 
포아송 로그 선형 모델은 독립 변수를 여러개 사용할 수 있는 모델입니다.  


### 예제

'''java
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class PoissonRegression {

    private double[] coefficients;

    public PoissonRegression() {
    }

    public void fit(double[][] X, double[] y) {
        // 행렬 차원 설정
        int n = X.length;
        int p = X[0].length;

        // 선형 회귀 모델을 사용하여 로그-선형 회귀를 수행
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, X);

        // 회귀 계수 추정
        this.coefficients = regression.estimateRegressionParameters();
    }

    public double predict(double[] x) {
        double logLambda = coefficients[0]; // 절편
        for (int i = 0; i < x.length; i++) {
            logLambda += coefficients[i + 1] * x[i];
        }
        return Math.exp(logLambda);
    }

    public static void main(String[] args) {
        // 예제 데이터: {기기 개수, 시간, 날짜}와 실제 사건 발생 횟수
        double[][] X = {
            {10, 0, 1}, // m, t, w
            {12, 1, 2},
            {15, 2, 3},
            {20, 3, 4},
            {22, 4, 5}
        };
        double[] y = {15, 20, 25, 30, 35}; // r

        // 모델 생성 및 적합
        PoissonRegression model = new PoissonRegression();
        model.fit(X, y);

        // 예측 값 출력
        double[] newObservation = {18, 1, 3}; // 새로운 관찰 값
        double prediction = model.predict(newObservation);
        System.out.println("예측 값: " + prediction);
    }
}

'''
